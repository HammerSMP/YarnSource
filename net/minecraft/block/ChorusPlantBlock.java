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
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class ChorusPlantBlock
extends ConnectingBlock {
    protected ChorusPlantBlock(AbstractBlock.Settings arg) {
        super(0.3125f, arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false)).with(DOWN, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return this.withConnectionProperties(arg.getWorld(), arg.getBlockPos());
    }

    public BlockState withConnectionProperties(BlockView arg, BlockPos arg2) {
        Block lv = arg.getBlockState(arg2.down()).getBlock();
        Block lv2 = arg.getBlockState(arg2.up()).getBlock();
        Block lv3 = arg.getBlockState(arg2.north()).getBlock();
        Block lv4 = arg.getBlockState(arg2.east()).getBlock();
        Block lv5 = arg.getBlockState(arg2.south()).getBlock();
        Block lv6 = arg.getBlockState(arg2.west()).getBlock();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, lv == this || lv == Blocks.CHORUS_FLOWER || lv == Blocks.END_STONE)).with(UP, lv2 == this || lv2 == Blocks.CHORUS_FLOWER)).with(NORTH, lv3 == this || lv3 == Blocks.CHORUS_FLOWER)).with(EAST, lv4 == this || lv4 == Blocks.CHORUS_FLOWER)).with(SOUTH, lv5 == this || lv5 == Blocks.CHORUS_FLOWER)).with(WEST, lv6 == this || lv6 == Blocks.CHORUS_FLOWER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
            return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
        }
        boolean bl = arg3.getBlock() == this || arg3.isOf(Blocks.CHORUS_FLOWER) || arg2 == Direction.DOWN && arg3.isOf(Blocks.END_STONE);
        return (BlockState)arg.with((Property)FACING_PROPERTIES.get(arg2), bl);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.breakBlock(arg3, true);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockState lv = arg2.getBlockState(arg3.down());
        boolean bl = !arg2.getBlockState(arg3.up()).isAir() && !lv.isAir();
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            BlockPos lv3 = arg3.offset(lv2);
            Block lv4 = arg2.getBlockState(lv3).getBlock();
            if (lv4 != this) continue;
            if (bl) {
                return false;
            }
            Block lv5 = arg2.getBlockState(lv3.down()).getBlock();
            if (lv5 != this && lv5 != Blocks.END_STONE) continue;
            return true;
        }
        Block lv6 = lv.getBlock();
        return lv6 == this || lv6 == Blocks.END_STONE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

