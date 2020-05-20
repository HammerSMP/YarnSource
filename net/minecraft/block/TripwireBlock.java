/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TripwireBlock
extends Block {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    public static final BooleanProperty DISARMED = Properties.DISARMED;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = HorizontalConnectingBlock.FACING_PROPERTIES;
    protected static final VoxelShape ATTACHED_SHAPE = Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
    protected static final VoxelShape DETACHED_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final TripwireHookBlock hookBlock;

    public TripwireBlock(TripwireHookBlock arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false)).with(ATTACHED, false)).with(DISARMED, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
        this.hookBlock = arg;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return arg.get(ATTACHED) != false ? ATTACHED_SHAPE : DETACHED_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.shouldConnectTo(lv.getBlockState(lv2.north()), Direction.NORTH))).with(EAST, this.shouldConnectTo(lv.getBlockState(lv2.east()), Direction.EAST))).with(SOUTH, this.shouldConnectTo(lv.getBlockState(lv2.south()), Direction.SOUTH))).with(WEST, this.shouldConnectTo(lv.getBlockState(lv2.west()), Direction.WEST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2.getAxis().isHorizontal()) {
            return (BlockState)arg.with(FACING_PROPERTIES.get(arg2), this.shouldConnectTo(arg3, arg2));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        this.update(arg2, arg3, arg);
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl || arg.isOf(arg4.getBlock())) {
            return;
        }
        this.update(arg2, arg3, (BlockState)arg.with(POWERED, true));
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        if (!arg.isClient && !arg4.getMainHandStack().isEmpty() && arg4.getMainHandStack().getItem() == Items.SHEARS) {
            arg.setBlockState(arg2, (BlockState)arg3.with(DISARMED, true), 4);
        }
        super.onBreak(arg, arg2, arg3, arg4);
    }

    private void update(World arg, BlockPos arg2, BlockState arg3) {
        block0: for (Direction lv : new Direction[]{Direction.SOUTH, Direction.WEST}) {
            for (int i = 1; i < 42; ++i) {
                BlockPos lv2 = arg2.offset(lv, i);
                BlockState lv3 = arg.getBlockState(lv2);
                if (lv3.isOf(this.hookBlock)) {
                    if (lv3.get(TripwireHookBlock.FACING) != lv.getOpposite()) continue block0;
                    this.hookBlock.update(arg, lv2, lv3, false, true, i, arg3);
                    continue block0;
                }
                if (!lv3.isOf(this)) continue block0;
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (arg2.isClient) {
            return;
        }
        if (arg.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePowered(arg2, arg3);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg2.getBlockState(arg3).get(POWERED).booleanValue()) {
            return;
        }
        this.updatePowered(arg2, arg3);
    }

    private void updatePowered(World arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        boolean bl = lv.get(POWERED);
        boolean bl2 = false;
        List<Entity> list = arg.getEntities(null, lv.getOutlineShape(arg, arg2).getBoundingBox().offset(arg2));
        if (!list.isEmpty()) {
            for (Entity lv2 : list) {
                if (lv2.canAvoidTraps()) continue;
                bl2 = true;
                break;
            }
        }
        if (bl2 != bl) {
            lv = (BlockState)lv.with(POWERED, bl2);
            arg.setBlockState(arg2, lv, 3);
            this.update(arg, arg2, lv);
        }
        if (bl2) {
            arg.getBlockTickScheduler().schedule(new BlockPos(arg2), this, 10);
        }
    }

    public boolean shouldConnectTo(BlockState arg, Direction arg2) {
        Block lv = arg.getBlock();
        if (lv == this.hookBlock) {
            return arg.get(TripwireHookBlock.FACING) == arg2.getOpposite();
        }
        return lv == this;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(SOUTH))).with(EAST, arg.get(WEST))).with(SOUTH, arg.get(NORTH))).with(WEST, arg.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(EAST))).with(EAST, arg.get(SOUTH))).with(SOUTH, arg.get(WEST))).with(WEST, arg.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(WEST))).with(EAST, arg.get(NORTH))).with(SOUTH, arg.get(EAST))).with(WEST, arg.get(SOUTH));
            }
        }
        return arg;
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        switch (arg2) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)arg.with(NORTH, arg.get(SOUTH))).with(SOUTH, arg.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)arg.with(EAST, arg.get(WEST))).with(WEST, arg.get(EAST));
            }
        }
        return super.mirror(arg, arg2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
    }
}

