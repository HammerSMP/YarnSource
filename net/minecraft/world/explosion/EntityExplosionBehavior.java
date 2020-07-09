/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.explosion;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class EntityExplosionBehavior
extends ExplosionBehavior {
    private final Entity entity;

    public EntityExplosionBehavior(Entity arg) {
        this.entity = arg;
    }

    @Override
    public Optional<Float> getBlastResistance(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, FluidState arg5) {
        return super.getBlastResistance(arg, arg2, arg3, arg4, arg5).map(float_ -> Float.valueOf(this.entity.getEffectiveExplosionResistance(arg, arg2, arg3, arg4, arg5, float_.floatValue())));
    }

    @Override
    public boolean canDestroyBlock(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, float f) {
        return this.entity.canExplosionDestroyBlock(arg, arg2, arg3, arg4, f);
    }
}

