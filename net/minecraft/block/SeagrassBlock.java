/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SeagrassBlock
extends PlantBlock
implements Fertilizable,
FluidFillable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    protected SeagrassBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isSideSolidFullSquare(arg2, arg3, Direction.UP) && !arg.isOf(Blocks.MAGMA_BLOCK);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        FluidState lv = arg.getWorld().getFluidState(arg.getBlockPos());
        if (lv.matches(FluidTags.WATER) && lv.getLevel() == 8) {
            return super.getPlacementState(arg);
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        BlockState lv = super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
        if (!lv.isAir()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        return lv;
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return true;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        BlockState lv = Blocks.TALL_SEAGRASS.getDefaultState();
        BlockState lv2 = (BlockState)lv.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
        BlockPos lv3 = arg2.up();
        if (arg.getBlockState(lv3).isOf(Blocks.WATER)) {
            arg.setBlockState(arg2, lv, 2);
            arg.setBlockState(lv3, lv2, 2);
        }
    }

    @Override
    public boolean canFillWithFluid(BlockView arg, BlockPos arg2, BlockState arg3, Fluid arg4) {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess arg, BlockPos arg2, BlockState arg3, FluidState arg4) {
        return false;
    }
}

