/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.explosion;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public enum DefaultExplosionBehavior implements ExplosionBehavior
{
    INSTANCE;


    @Override
    public Optional<Float> getBlastResistance(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, FluidState arg5) {
        if (arg4.isAir() && arg5.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Float.valueOf(Math.max(arg4.getBlock().getBlastResistance(), arg5.getBlastResistance())));
    }

    @Override
    public boolean canDestroyBlock(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, float f) {
        return true;
    }
}

