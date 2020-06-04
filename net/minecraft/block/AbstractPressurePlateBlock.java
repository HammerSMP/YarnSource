/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AbstractPressurePlateBlock
extends Block {
    protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
    protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
    protected static final Box BOX = new Box(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

    protected AbstractPressurePlateBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.getRedstoneOutput(arg) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
    }

    protected int getTickRate() {
        return 20;
    }

    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.DOWN && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        return AbstractPressurePlateBlock.hasTopRim(arg2, lv) || AbstractPressurePlateBlock.sideCoversSmallSquare(arg2, lv, Direction.UP);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        int i = this.getRedstoneOutput(arg);
        if (i > 0) {
            this.updatePlateState(arg2, arg3, arg, i);
        }
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (arg2.isClient) {
            return;
        }
        int i = this.getRedstoneOutput(arg);
        if (i == 0) {
            this.updatePlateState(arg2, arg3, arg, i);
        }
    }

    protected void updatePlateState(World arg, BlockPos arg2, BlockState arg3, int i) {
        boolean bl2;
        int j = this.getRedstoneOutput(arg, arg2);
        boolean bl = i > 0;
        boolean bl3 = bl2 = j > 0;
        if (i != j) {
            BlockState lv = this.setRedstoneOutput(arg3, j);
            arg.setBlockState(arg2, lv, 2);
            this.updateNeighbors(arg, arg2);
            arg.scheduleBlockRerenderIfNeeded(arg2, arg3, lv);
        }
        if (!bl2 && bl) {
            this.playDepressSound(arg, arg2);
        } else if (bl2 && !bl) {
            this.playPressSound(arg, arg2);
        }
        if (bl2) {
            arg.getBlockTickScheduler().schedule(new BlockPos(arg2), this, this.getTickRate());
        }
    }

    protected abstract void playPressSound(WorldAccess var1, BlockPos var2);

    protected abstract void playDepressSound(WorldAccess var1, BlockPos var2);

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl || arg.isOf(arg4.getBlock())) {
            return;
        }
        if (this.getRedstoneOutput(arg) > 0) {
            this.updateNeighbors(arg2, arg3);
        }
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
    }

    protected void updateNeighbors(World arg, BlockPos arg2) {
        arg.updateNeighborsAlways(arg2, this);
        arg.updateNeighborsAlways(arg2.down(), this);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return this.getRedstoneOutput(arg);
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (arg4 == Direction.UP) {
            return this.getRedstoneOutput(arg);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return PistonBehavior.DESTROY;
    }

    protected abstract int getRedstoneOutput(World var1, BlockPos var2);

    protected abstract int getRedstoneOutput(BlockState var1);

    protected abstract BlockState setRedstoneOutput(BlockState var1, int var2);
}

