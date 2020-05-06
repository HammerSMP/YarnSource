/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.entity.EquipmentSlot;

public class InfinityEnchantment
extends Enchantment {
    public InfinityEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.BOW, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 20;
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
    public boolean canAccept(Enchantment arg) {
        if (arg instanceof MendingEnchantment) {
            return false;
        }
        return super.canAccept(arg);
    }
}

