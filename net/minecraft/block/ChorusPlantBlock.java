/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ChorusPlantBlock
extends ConnectingBlock {
    protected ChorusPlantBlock(AbstractBlock.Settings arg) {
        super(0.3125f, arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false)).with(DOWN, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
    }

    public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
        Block lv = world.getBlockState(pos.down()).getBlock();
        Block lv2 = world.getBlockState(pos.up()).getBlock();
        Block lv3 = world.getBlockState(pos.north()).getBlock();
        Block lv4 = world.getBlockState(pos.east()).getBlock();
        Block lv5 = world.getBlockState(pos.south()).getBlock();
        Block lv6 = world.getBlockState(pos.west()).getBlock();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, lv == this || lv == Blocks.CHORUS_FLOWER || lv == Blocks.END_STONE)).with(UP, lv2 == this || lv2 == Blocks.CHORUS_FLOWER)).with(NORTH, lv3 == this || lv3 == Blocks.CHORUS_FLOWER)).with(EAST, lv4 == this || lv4 == Blocks.CHORUS_FLOWER)).with(SOUTH, lv5 == this || lv5 == Blocks.CHORUS_FLOWER)).with(WEST, lv6 == this || lv6 == Blocks.CHORUS_FLOWER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (!state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
            return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        }
        boolean bl = newState.getBlock() == this || newState.isOf(Blocks.CHORUS_FLOWER) || direction == Direction.DOWN && newState.isOf(Blocks.END_STONE);
        return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), bl);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState lv = world.getBlockState(pos.down());
        boolean bl = !world.getBlockState(pos.up()).isAir() && !lv.isAir();
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            BlockPos lv3 = pos.offset(lv2);
            Block lv4 = world.getBlockState(lv3).getBlock();
            if (lv4 != this) continue;
            if (bl) {
                return false;
            }
            Block lv5 = world.getBlockState(lv3.down()).getBlock();
            if (lv5 != this && lv5 != Blocks.END_STONE) continue;
            return true;
        }
        Block lv6 = lv.getBlock();
        return lv6 == this || lv6 == Blocks.END_STONE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

