/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DetectorRailBlock
extends AbstractRailBlock {
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public DetectorRailBlock(AbstractBlock.Settings arg) {
        super(true, arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false)).with(SHAPE, RailShape.NORTH_SOUTH));
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (arg2.isClient) {
            return;
        }
        if (arg.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePoweredStatus(arg2, arg3, arg);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePoweredStatus(arg2, arg3, arg);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (!arg.get(POWERED).booleanValue()) {
            return 0;
        }
        return arg4 == Direction.UP ? 15 : 0;
    }

    private void updatePoweredStatus(World arg, BlockPos arg2, BlockState arg3) {
        boolean bl = arg3.get(POWERED);
        boolean bl2 = false;
        List<AbstractMinecartEntity> list = this.getCarts(arg, arg2, AbstractMinecartEntity.class, null);
        if (!list.isEmpty()) {
            bl2 = true;
        }
        if (bl2 && !bl) {
            BlockState lv = (BlockState)arg3.with(POWERED, true);
            arg.setBlockState(arg2, lv, 3);
            this.updateNearbyRails(arg, arg2, lv, true);
            arg.updateNeighborsAlways(arg2, this);
            arg.updateNeighborsAlways(arg2.down(), this);
            arg.scheduleBlockRerenderIfNeeded(arg2, arg3, lv);
        }
        if (!bl2 && bl) {
            BlockState lv2 = (BlockState)arg3.with(POWERED, false);
            arg.setBlockState(arg2, lv2, 3);
            this.updateNearbyRails(arg, arg2, lv2, false);
            arg.updateNeighborsAlways(arg2, this);
            arg.updateNeighborsAlways(arg2.down(), this);
            arg.scheduleBlockRerenderIfNeeded(arg2, arg3, lv2);
        }
        if (bl2) {
            arg.getBlockTickScheduler().schedule(arg2, this, 20);
        }
        arg.updateComparators(arg2, this);
    }

    protected void updateNearbyRails(World arg, BlockPos arg2, BlockState arg3, boolean bl) {
        RailPlacementHelper lv = new RailPlacementHelper(arg, arg2, arg3);
        List<BlockPos> list = lv.getNeighbors();
        for (BlockPos lv2 : list) {
            BlockState lv3 = arg.getBlockState(lv2);
            lv3.neighborUpdate(arg, lv2, lv3.getBlock(), arg2, false);
        }
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        this.updatePoweredStatus(arg2, arg3, this.updateCurves(arg, arg2, arg3, bl));
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        if (arg.get(POWERED).booleanValue()) {
            List<CommandBlockMinecartEntity> list = this.getCarts(arg2, arg3, CommandBlockMinecartEntity.class, null);
            if (!list.isEmpty()) {
                return list.get(0).getCommandExecutor().getSuccessCount();
            }
            List<AbstractMinecartEntity> list2 = this.getCarts(arg2, arg3, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
            if (!list2.isEmpty()) {
                return ScreenHandler.calculateComparatorOutput((Inventory)((Object)list2.get(0)));
            }
        }
        return 0;
    }

    protected <T extends AbstractMinecartEntity> List<T> getCarts(World arg, BlockPos arg2, Class<T> class_, @Nullable Predicate<Entity> predicate) {
        return arg.getEntities(class_, this.getCartDetectionBox(arg2), predicate);
    }

    private Box getCartDetectionBox(BlockPos arg) {
        double d = 0.2;
        return new Box((double)arg.getX() + 0.2, arg.getY(), (double)arg.getZ() + 0.2, (double)(arg.getX() + 1) - 0.2, (double)(arg.getY() + 1) - 0.2, (double)(arg.getZ() + 1) - 0.2);
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case CLOCKWISE_180: {
                switch (arg.get(SHAPE)) {
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (arg.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (arg.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
            }
        }
        return arg;
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        RailShape lv = arg.get(SHAPE);
        switch (arg2) {
            case LEFT_RIGHT: {
                switch (lv) {
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
                break;
            }
            case FRONT_BACK: {
                switch (lv) {
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
                break;
            }
        }
        return super.mirror(arg, arg2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(SHAPE, POWERED);
    }
}

