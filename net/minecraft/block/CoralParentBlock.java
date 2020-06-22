/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CoralParentBlock
extends Block
implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    protected CoralParentBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WATERLOGGED, true));
    }

    protected void checkLivingConditions(BlockState arg, WorldAccess arg2, BlockPos arg3) {
        if (!CoralParentBlock.isInWater(arg, arg2, arg3)) {
            arg2.getBlockTickScheduler().schedule(arg3, this, 60 + arg2.getRandom().nextInt(40));
        }
    }

    protected static boolean isInWater(BlockState arg, BlockView arg2, BlockPos arg3) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return true;
        }
        for (Direction lv : Direction.values()) {
            if (!arg2.getFluidState(arg3.offset(lv)).isIn(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        FluidState lv;
        return (BlockState)this.getDefaultState().with(WATERLOGGED, (lv = arg.getWorld().getFluidState(arg.getBlockPos())).isIn(FluidTags.WATER) && lv.getLevel() == 8);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg2 == Direction.DOWN && !this.canPlaceAt(arg, arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        return arg2.getBlockState(lv).isSideSolidFullSquare(arg2, lv, Direction.UP);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }
}

