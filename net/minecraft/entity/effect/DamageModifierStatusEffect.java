/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class DamageModifierStatusEffect
extends StatusEffect {
    protected final double modifier;

    protected DamageModifierStatusEffect(StatusEffectType arg, int i, double d) {
        super(arg, i);
        this.modifier = d;
    }

    @Override
    public double adjustModifierAmount(int i, EntityAttributeModifier arg) {
        return this.modifier * (double)(i + 1);
    }
}

