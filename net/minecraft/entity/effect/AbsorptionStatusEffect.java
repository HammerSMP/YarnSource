/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class AbsorptionStatusEffect
extends StatusEffect {
    protected AbsorptionStatusEffect(StatusEffectType arg, int i) {
        super(arg, i);
    }

    @Override
    public void onRemoved(LivingEntity arg, AttributeContainer arg2, int i) {
        arg.setAbsorptionAmount(arg.getAbsorptionAmount() - (float)(4 * (i + 1)));
        super.onRemoved(arg, arg2, i);
    }

    @Override
    public void onApplied(LivingEntity arg, AttributeContainer arg2, int i) {
        arg.setAbsorptionAmount(arg.getAbsorptionAmount() + (float)(4 * (i + 1)));
        super.onApplied(arg, arg2, i);
    }
}

