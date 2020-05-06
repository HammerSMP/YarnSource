/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class ThornsEnchantment
extends Enchantment {
    public ThornsEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.ARMOR_CHEST, args);
    }

    @Override
    public int getMinimumPower(int i) {
        return 10 + 20 * (i - 1);
    }

    @Override
    public int getMaximumPower(int i) {
        return super.getMinimumPower(i) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack arg) {
        if (arg.getItem() instanceof ArmorItem) {
            return true;
        }
        return super.isAcceptableItem(arg);
    }

    @Override
    public void onUserDamaged(LivingEntity arg2, Entity arg22, int i) {
        Random random = arg2.getRandom();
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.THORNS, arg2);
        if (ThornsEnchantment.shouldDamageAttacker(i, random)) {
            if (arg22 != null) {
                arg22.damage(DamageSource.thorns(arg2), ThornsEnchantment.getDamageAmount(i, random));
            }
            if (entry != null) {
                entry.getValue().damage(3, arg2, arg -> arg.sendEquipmentBreakStatus((EquipmentSlot)((Object)((Object)entry.getKey()))));
            }
        } else if (entry != null) {
            entry.getValue().damage(1, arg2, arg -> arg.sendEquipmentBreakStatus((EquipmentSlot)((Object)((Object)entry.getKey()))));
        }
    }

    public static boolean shouldDamageAttacker(int i, Random random) {
        if (i <= 0) {
            return false;
        }
        return random.nextFloat() < 0.15f * (float)i;
    }

    public static int getDamageAmount(int i, Random random) {
        if (i > 10) {
            return i - 10;
        }
        return 1 + random.nextInt(4);
    }
}

