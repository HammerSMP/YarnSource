/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;

public abstract class AbstractSittingPhase
extends AbstractPhase {
    public AbstractSittingPhase(EnderDragonEntity arg) {
        super(arg);
    }

    @Override
    public boolean isSittingOrHovering() {
        return true;
    }

    @Override
    public float modifyDamageTaken(DamageSource arg, float f) {
        if (arg.getSource() instanceof PersistentProjectileEntity) {
            arg.getSource().setOnFireFor(1);
            return 0.0f;
        }
        return super.modifyDamageTaken(arg, f);
    }
}

