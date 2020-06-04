/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class UnbreakingEnchantment
extends Enchantment {
    protected UnbreakingEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.BREAKABLE, args);
    }

    @Override
    public int getMinPower(int i) {
        return 5 + (i - 1) * 8;
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
    public boolean isAcceptableItem(ItemStack arg) {
        if (arg.isDamageable()) {
            return true;
        }
        return super.isAcceptableItem(arg);
    }

    public static boolean shouldPreventDamage(ItemStack arg, int i, Random random) {
        if (arg.getItem() instanceof ArmorItem && random.nextFloat() < 0.6f) {
            return false;
        }
        return random.nextInt(i + 1) > 0;
    }
}

