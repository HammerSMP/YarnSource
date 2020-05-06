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
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class CoralBlockBlock
extends Block {
    private final Block deadCoralBlock;

    public CoralBlockBlock(Block arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.deadCoralBlock = arg;
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!this.isInWater(arg2, arg3)) {
            arg2.setBlockState(arg3, this.deadCoralBlock.getDefaultState(), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (!this.isInWater(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 60 + arg4.getRandom().nextInt(40));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    protected boolean isInWater(BlockView arg, BlockPos arg2) {
        for (Direction lv : Direction.values()) {
            FluidState lv2 = arg.getFluidState(arg2.offset(lv));
            if (!lv2.matches(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        if (!this.isInWater(arg.getWorld(), arg.getBlockPos())) {
            arg.getWorld().getBlockTickScheduler().schedule(arg.getBlockPos(), this, 60 + arg.getWorld().getRandom().nextInt(40));
        }
        return this.getDefaultState();
    }
}

