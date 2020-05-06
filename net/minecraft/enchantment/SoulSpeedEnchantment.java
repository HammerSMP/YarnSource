/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class SoulSpeedEnchantment
extends Enchantment {
    public SoulSpeedEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.ARMOR_FEET, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return i * 10;
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + 15;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }
}

