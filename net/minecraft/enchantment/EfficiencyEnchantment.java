/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EfficiencyEnchantment
extends Enchantment {
    protected EfficiencyEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.DIGGER, args);
    }

    @Override
    public int getMinPower(int i) {
        return 1 + 10 * (i - 1);
    }

    @Override
    public int getMaxPower(int i) {
        return super.getMinPower(i) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack arg) {
        if (arg.getItem() == Items.SHEARS) {
            return true;
        }
        return super.isAcceptableItem(arg);
    }
}

