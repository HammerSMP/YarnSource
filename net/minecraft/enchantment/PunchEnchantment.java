/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment
extends Enchantment {
    public PunchEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.BOW, args);
    }

    @Override
    public int getMinPower(int i) {
        return 12 + (i - 1) * 20;
    }

    @Override
    public int getMaxPower(int i) {
        return this.getMinPower(i) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

