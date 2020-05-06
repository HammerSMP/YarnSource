/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralWallFanBlock
extends DeadCoralWallFanBlock {
    private final Block deadCoralBlock;

    protected CoralWallFanBlock(Block arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.deadCoralBlock = arg;
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        this.checkLivingConditions(arg, arg2, arg3);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!CoralWallFanBlock.isInWater(arg, arg2, arg3)) {
            arg2.setBlockState(arg3, (BlockState)((BlockState)this.deadCoralBlock.getDefaultState().with(WATERLOGGED, false)).with(FACING, arg.get(FACING)), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2.getOpposite() == arg.get(FACING) && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        this.checkLivingConditions(arg, arg4, arg5);
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }
}

