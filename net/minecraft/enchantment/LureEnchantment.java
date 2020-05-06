/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class LureEnchantment
extends Enchantment {
    protected LureEnchantment(Enchantment.Rarity arg, EnchantmentTarget arg2, EquipmentSlot ... args) {
        super(arg, arg2, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 15 + (i - 1) * 9;
    }

    @Override
    public int getMaximumPower(int i) {
        return super.getMinimumPower(i) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }
}

