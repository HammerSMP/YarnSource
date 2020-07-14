/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralFanBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CoralFanBlock
extends DeadCoralFanBlock {
    private final Block deadCoralBlock;

    protected CoralFanBlock(Block deadCoralBlock, AbstractBlock.Settings settings) {
        super(settings);
        this.deadCoralBlock = deadCoralBlock;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.checkLivingConditions(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!CoralFanBlock.isInWater(state, world, pos)) {
            world.setBlockState(pos, (BlockState)this.deadCoralBlock.getDefaultState().with(WATERLOGGED, false), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        this.checkLivingConditions(state, world, pos);
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }
}

