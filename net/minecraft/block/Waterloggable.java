/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public interface Waterloggable
extends FluidDrainable,
FluidFillable {
    @Override
    default public boolean canFillWithFluid(BlockView arg, BlockPos arg2, BlockState arg3, Fluid arg4) {
        return arg3.get(Properties.WATERLOGGED) == false && arg4 == Fluids.WATER;
    }

    @Override
    default public boolean tryFillWithFluid(IWorld arg, BlockPos arg2, BlockState arg3, FluidState arg4) {
        if (!arg3.get(Properties.WATERLOGGED).booleanValue() && arg4.getFluid() == Fluids.WATER) {
            if (!arg.isClient()) {
                arg.setBlockState(arg2, (BlockState)arg3.with(Properties.WATERLOGGED, true), 3);
                arg.getFluidTickScheduler().schedule(arg2, arg4.getFluid(), arg4.getFluid().getTickRate(arg));
            }
            return true;
        }
        return false;
    }

    @Override
    default public Fluid tryDrainFluid(IWorld arg, BlockPos arg2, BlockState arg3) {
        if (arg3.get(Properties.WATERLOGGED).booleanValue()) {
            arg.setBlockState(arg2, (BlockState)arg3.with(Properties.WATERLOGGED, false), 3);
            return Fluids.WATER;
        }
        return Fluids.EMPTY;
    }
}

