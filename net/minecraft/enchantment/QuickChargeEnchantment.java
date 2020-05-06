/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class QuickChargeEnchantment
extends Enchantment {
    public QuickChargeEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.CROSSBOW, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 12 + (i - 1) * 20;
    }

    @Override
    public int getMaximumPower(int i) {
        return 50;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }
}

