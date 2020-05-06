/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;

public class DamageRecord {
    private final DamageSource damageSource;
    private final int entityAge;
    private final float damage;
    private final float entityHealth;
    private final String fallDeathSuffix;
    private final float fallDistance;

    public DamageRecord(DamageSource arg, int i, float f, float g, String string, float h) {
        this.damageSource = arg;
        this.entityAge = i;
        this.damage = g;
        this.entityHealth = f;
        this.fallDeathSuffix = string;
        this.fallDistance = h;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public float getDamage() {
        return this.damage;
    }

    public boolean isAttackerLiving() {
        return this.damageSource.getAttacker() instanceof LivingEntity;
    }

    @Nullable
    public String getFallDeathSuffix() {
        return this.fallDeathSuffix;
    }

    @Nullable
    public Text getAttackerName() {
        return this.getDamageSource().getAttacker() == null ? null : this.getDamageSource().getAttacker().getDisplayName();
    }

    public float getFallDistance() {
        if (this.damageSource == DamageSource.OUT_OF_WORLD) {
            return Float.MAX_VALUE;
        }
        return this.fallDistance;
    }
}

