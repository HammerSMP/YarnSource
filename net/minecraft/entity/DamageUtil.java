/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;

public class DamageUtil {
    public static float getDamageLeft(float damage, float armor, float armorToughness) {
        float i = 2.0f + armorToughness / 4.0f;
        float j = MathHelper.clamp(armor - damage / i, armor * 0.2f, 20.0f);
        return damage * (1.0f - j / 25.0f);
    }

    public static float getInflictedDamage(float damageDealt, float protection) {
        float h = MathHelper.clamp(protection, 0.0f, 20.0f);
        return damageDealt * (1.0f - h / 25.0f);
    }
}

