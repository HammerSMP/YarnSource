/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.class_5362;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

public enum class_5360 implements class_5362
{
    INSTANCE;


    @Override
    public Optional<Float> method_29555(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, FluidState arg5) {
        if (arg4.isAir() && arg5.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Float.valueOf(Math.max(arg4.getBlock().getBlastResistance(), arg5.getBlastResistance())));
    }

    @Override
    public boolean method_29554(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, float f) {
        return true;
    }
}

