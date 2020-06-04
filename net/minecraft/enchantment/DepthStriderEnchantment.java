/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class DepthStriderEnchantment
extends Enchantment {
    public DepthStriderEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.ARMOR_FEET, args);
    }

    @Override
    public int getMinPower(int i) {
        return i * 10;
    }

    @Override
    public int getMaxPower(int i) {
        return this.getMinPower(i) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canAccept(Enchantment arg) {
        return super.canAccept(arg) && arg != Enchantments.FROST_WALKER;
    }
}

