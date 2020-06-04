/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class LoyaltyEnchantment
extends Enchantment {
    public LoyaltyEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.TRIDENT, args);
    }

    @Override
    public int getMinPower(int i) {
        return 5 + i * 7;
    }

    @Override
    public int getMaxPower(int i) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canAccept(Enchantment arg) {
        return super.canAccept(arg);
    }
}

