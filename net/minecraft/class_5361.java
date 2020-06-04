/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.class_5360;
import net.minecraft.class_5362;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

class class_5361
implements class_5362 {
    private final Entity field_25399;

    class_5361(Entity arg) {
        this.field_25399 = arg;
    }

    @Override
    public Optional<Float> method_29555(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, FluidState arg5) {
        return class_5360.INSTANCE.method_29555(arg, arg2, arg3, arg4, arg5).map(float_ -> Float.valueOf(this.field_25399.getEffectiveExplosionResistance(arg, arg2, arg3, arg4, arg5, float_.floatValue())));
    }

    @Override
    public boolean method_29554(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, float f) {
        return this.field_25399.canExplosionDestroyBlock(arg, arg2, arg3, arg4, f);
    }
}

