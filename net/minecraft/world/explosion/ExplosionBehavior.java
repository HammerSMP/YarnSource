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

public class ExplosionBehavior {
    public Optional<Float> getBlastResistance(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, FluidState arg5) {
        if (arg4.isAir() && arg5.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Float.valueOf(Math.max(arg4.getBlock().getBlastResistance(), arg5.getBlastResistance())));
    }

    public boolean canDestroyBlock(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, float f) {
        return true;
    }
}

