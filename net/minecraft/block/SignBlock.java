/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SignBlock
extends AbstractSignBlock {
    public static final IntProperty ROTATION = Properties.ROTATION;

    public SignBlock(AbstractBlock.Settings arg, SignType arg2) {
        super(arg, arg2);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ROTATION, 0)).with(WATERLOGGED, false));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState lv;
        return (BlockState)((BlockState)this.getDefaultState().with(ROTATION, MathHelper.floor((double)((180.0f + ctx.getPlayerYaw()) * 16.0f / 360.0f) + 0.5) & 0xF)).with(WATERLOGGED, (lv = ctx.getWorld().getFluidState(ctx.getBlockPos())).getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.DOWN && !this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(ROTATION, rotation.rotate(state.get(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with(ROTATION, mirror.mirror(state.get(ROTATION), 16));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED);
    }
}

