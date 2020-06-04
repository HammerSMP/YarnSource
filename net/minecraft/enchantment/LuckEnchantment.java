/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class LuckEnchantment
extends Enchantment {
    protected LuckEnchantment(Enchantment.Rarity arg, EnchantmentTarget arg2, EquipmentSlot ... args) {
        super(arg, arg2, args);
    }

    @Override
    public int getMinPower(int i) {
        return 15 + (i - 1) * 9;
    }

    @Override
    public int getMaxPower(int i) {
        return super.getMinPower(i) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canAccept(Enchantment arg) {
        return super.canAccept(arg) && arg != Enchantments.SILK_TOUCH;
    }
}

