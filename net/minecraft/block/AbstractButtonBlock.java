/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractButtonBlock
extends WallMountedBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final VoxelShape CEILING_X_SHAPE = Block.createCuboidShape(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape CEILING_Z_SHAPE = Block.createCuboidShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape FLOOR_X_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
    protected static final VoxelShape FLOOR_Z_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
    protected static final VoxelShape CEILING_X_PRESSED_SHAPE = Block.createCuboidShape(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape CEILING_Z_PRESSED_SHAPE = Block.createCuboidShape(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape FLOOR_X_PRESSED_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
    protected static final VoxelShape FLOOR_Z_PRESSED_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
    protected static final VoxelShape NORTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
    protected static final VoxelShape WEST_PRESSED_SHAPE = Block.createCuboidShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_PRESSED_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
    private final boolean wooden;

    protected AbstractButtonBlock(boolean bl, AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, WallMountLocation.WALL));
        this.wooden = bl;
    }

    private int getPressTicks() {
        return this.wooden ? 30 : 20;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        Direction lv = arg.get(FACING);
        boolean bl = arg.get(POWERED);
        switch ((WallMountLocation)arg.get(FACE)) {
            case FLOOR: {
                if (lv.getAxis() == Direction.Axis.X) {
                    return bl ? FLOOR_X_PRESSED_SHAPE : FLOOR_X_SHAPE;
                }
                return bl ? FLOOR_Z_PRESSED_SHAPE : FLOOR_Z_SHAPE;
            }
            case WALL: {
                switch (lv) {
                    case EAST: {
                        return bl ? EAST_PRESSED_SHAPE : EAST_SHAPE;
                    }
                    case WEST: {
                        return bl ? WEST_PRESSED_SHAPE : WEST_SHAPE;
                    }
                    case SOUTH: {
                        return bl ? SOUTH_PRESSED_SHAPE : SOUTH_SHAPE;
                    }
                }
                return bl ? NORTH_PRESSED_SHAPE : NORTH_SHAPE;
            }
        }
        if (lv.getAxis() == Direction.Axis.X) {
            return bl ? CEILING_X_PRESSED_SHAPE : CEILING_X_SHAPE;
        }
        return bl ? CEILING_Z_PRESSED_SHAPE : CEILING_Z_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg.get(POWERED).booleanValue()) {
            return ActionResult.CONSUME;
        }
        this.powerOn(arg, arg2, arg3);
        this.playClickSound(arg4, arg2, arg3, true);
        return ActionResult.success(arg2.isClient);
    }

    public void powerOn(BlockState arg, World arg2, BlockPos arg3) {
        arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, true), 3);
        this.updateNeighbors(arg, arg2, arg3);
        arg2.getBlockTickScheduler().schedule(arg3, this, this.getPressTicks());
    }

    protected void playClickSound(@Nullable PlayerEntity arg, WorldAccess arg2, BlockPos arg3, boolean bl) {
        arg2.playSound(bl ? arg : null, arg3, this.getClickSound(bl), SoundCategory.BLOCKS, 0.3f, bl ? 0.6f : 0.5f);
    }

    protected abstract SoundEvent getClickSound(boolean var1);

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl || arg.isOf(arg4.getBlock())) {
            return;
        }
        if (arg.get(POWERED).booleanValue()) {
            this.updateNeighbors(arg, arg2, arg3);
        }
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (arg.get(POWERED).booleanValue() && AbstractButtonBlock.getDirection(arg) == arg4) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.get(POWERED).booleanValue()) {
            return;
        }
        if (this.wooden) {
            this.tryPowerWithProjectiles(arg, arg2, arg3);
        } else {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, false), 3);
            this.updateNeighbors(arg, arg2, arg3);
            this.playClickSound(null, arg2, arg3, false);
        }
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (arg2.isClient || !this.wooden || arg.get(POWERED).booleanValue()) {
            return;
        }
        this.tryPowerWithProjectiles(arg, arg2, arg3);
    }

    private void tryPowerWithProjectiles(BlockState arg, World arg2, BlockPos arg3) {
        boolean bl2;
        List<PersistentProjectileEntity> list = arg2.getNonSpectatingEntities(PersistentProjectileEntity.class, arg.getOutlineShape(arg2, arg3).getBoundingBox().offset(arg3));
        boolean bl = !list.isEmpty();
        if (bl != (bl2 = arg.get(POWERED).booleanValue())) {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, bl), 3);
            this.updateNeighbors(arg, arg2, arg3);
            this.playClickSound(null, arg2, arg3, bl);
        }
        if (bl) {
            arg2.getBlockTickScheduler().schedule(new BlockPos(arg3), this, this.getPressTicks());
        }
    }

    private void updateNeighbors(BlockState arg, World arg2, BlockPos arg3) {
        arg2.updateNeighborsAlways(arg3, this);
        arg2.updateNeighborsAlways(arg3.offset(AbstractButtonBlock.getDirection(arg).getOpposite()), this);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, POWERED, FACE);
    }
}

