/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AquaAffinityEnchantment
extends Enchantment {
    public AquaAffinityEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.ARMOR_HEAD, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 1;
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + 40;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }
}

