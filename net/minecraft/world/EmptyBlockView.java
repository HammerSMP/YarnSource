/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public enum EmptyBlockView implements BlockView
{
    INSTANCE;


    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos arg) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        return Fluids.EMPTY.getDefaultState();
    }
}

