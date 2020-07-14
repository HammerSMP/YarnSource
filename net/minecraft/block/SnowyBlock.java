/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class SnowyBlock
extends Block {
    public static final BooleanProperty SNOWY = Properties.SNOWY;

    protected SnowyBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(SNOWY, false));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.UP) {
            return (BlockState)state.with(SNOWY, newState.isOf(Blocks.SNOW_BLOCK) || newState.isOf(Blocks.SNOW));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState lv;
        return (BlockState)this.getDefaultState().with(SNOWY, (lv = ctx.getWorld().getBlockState(ctx.getBlockPos().up())).isOf(Blocks.SNOW_BLOCK) || lv.isOf(Blocks.SNOW));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SNOWY);
    }
}

