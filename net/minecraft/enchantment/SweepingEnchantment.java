/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class SweepingEnchantment
extends Enchantment {
    public SweepingEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.WEAPON, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 9;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public static float getMultiplier(int i) {
        return 1.0f - 1.0f / (float)(i + 1);
    }
}

