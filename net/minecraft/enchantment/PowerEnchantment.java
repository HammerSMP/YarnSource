/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class PowerEnchantment
extends Enchantment {
    public PowerEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.BOW, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 1 + (i - 1) * 10;
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + 15;
    }

    @Override
    public int getMaximumLevel() {
        return 5;
    }
}

