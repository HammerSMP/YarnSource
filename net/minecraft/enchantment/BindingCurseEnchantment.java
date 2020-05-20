/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class BindingCurseEnchantment
extends Enchantment {
    public BindingCurseEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.WEARABLE, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 25;
    }

    @Override
    public int getMaximumPower(int i) {
        return 50;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isCursed() {
        return true;
    }
}
