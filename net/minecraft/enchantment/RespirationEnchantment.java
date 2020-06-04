/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class RespirationEnchantment
extends Enchantment {
    public RespirationEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.ARMOR_HEAD, args);
    }

    @Override
    public int getMinPower(int i) {
        return 10 * i;
    }

    @Override
    public int getMaxPower(int i) {
        return this.getMinPower(i) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}

