/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public final class VerticalBlockSample
implements BlockView {
    private final BlockState[] states;

    public VerticalBlockSample(BlockState[] args) {
        this.states = args;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos arg) {
        int i = arg.getY();
        if (i < 0 || i >= this.states.length) {
            return Blocks.AIR.getDefaultState();
        }
        return this.states[i];
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        return this.getBlockState(arg).getFluidState();
    }
}

