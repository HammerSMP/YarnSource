/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class KnockbackEnchantment
extends Enchantment {
    protected KnockbackEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.WEAPON, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 5 + 20 * (i - 1);
    }

    @Override
    public int getMaximumPower(int i) {
        return super.getMinimumPower(i) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 2;
    }
}

