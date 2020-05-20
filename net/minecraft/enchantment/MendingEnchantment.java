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
    public int getMinimumPower(int i) {
        return i * 25;
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + 50;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }
}
