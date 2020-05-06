/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class ImpalingEnchantment
extends Enchantment {
    public ImpalingEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.TRIDENT, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 1 + (i - 1) * 8;
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + 20;
    }

    @Override
    public int getMaximumLevel() {
        return 5;
    }

    @Override
    public float getAttackDamage(int i, EntityGroup arg) {
        if (arg == EntityGroup.AQUATIC) {
            return (float)i * 2.5f;
        }
        return 0.0f;
    }
}

