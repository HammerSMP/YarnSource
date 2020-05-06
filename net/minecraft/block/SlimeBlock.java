/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SlimeBlock
extends TransparentBlock {
    public SlimeBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public void onLandedUpon(World arg, BlockPos arg2, Entity arg3, float f) {
        if (arg3.bypassesLandingEffects()) {
            super.onLandedUpon(arg, arg2, arg3, f);
        } else {
            arg3.handleFallDamage(f, 0.0f);
        }
    }

    @Override
    public void onEntityLand(BlockView arg, Entity arg2) {
        if (arg2.bypassesLandingEffects()) {
            super.onEntityLand(arg, arg2);
        } else {
            this.bounce(arg2);
        }
    }

    private void bounce(Entity arg) {
        Vec3d lv = arg.getVelocity();
        if (lv.y < 0.0) {
            double d = arg instanceof LivingEntity ? 1.0 : 0.8;
            arg.setVelocity(lv.x, -lv.y * d, lv.z);
        }
    }

    @Override
    public void onSteppedOn(World arg, BlockPos arg2, Entity arg3) {
        double d = Math.abs(arg3.getVelocity().y);
        if (d < 0.1 && !arg3.bypassesSteppingEffects()) {
            double e = 0.4 + d * 0.2;
            arg3.setVelocity(arg3.getVelocity().multiply(e, 1.0, e));
        }
        super.onSteppedOn(arg, arg2, arg3);
    }
}

