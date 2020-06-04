/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class HealthBoostStatusEffect
extends StatusEffect {
    public HealthBoostStatusEffect(StatusEffectType arg, int i) {
        super(arg, i);
    }

    @Override
    public void onRemoved(LivingEntity arg, AttributeContainer arg2, int i) {
        super.onRemoved(arg, arg2, i);
        if (arg.getHealth() > arg.getMaxHealth()) {
            arg.setHealth(arg.getMaxHealth());
        }
    }
}

