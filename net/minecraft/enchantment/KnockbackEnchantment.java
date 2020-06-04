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
    public int getMinPower(int i) {
        return 5 + 20 * (i - 1);
    }

    @Override
    public int getMaxPower(int i) {
        return super.getMinPower(i) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

