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
    public int getMinimumPower(int i) {
        return 10 * i;
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + 30;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }
}

