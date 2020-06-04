/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class MendingEnchantment
extends Enchantment {
    public MendingEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.BREAKABLE, args);
    }

    @Override
    public int getMinPower(int i) {
        return i * 25;
    }

    @Override
    public int getMaxPower(int i) {
        return this.getMinPower(i) + 50;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}

