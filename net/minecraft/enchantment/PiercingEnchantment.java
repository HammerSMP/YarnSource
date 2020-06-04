/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class PiercingEnchantment
extends Enchantment {
    public PiercingEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.CROSSBOW, args);
    }

    @Override
    public int getMinPower(int i) {
        return 1 + (i - 1) * 10;
    }

    @Override
    public int getMaxPower(int i) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean canAccept(Enchantment arg) {
        return super.canAccept(arg) && arg != Enchantments.MULTISHOT;
    }
}

