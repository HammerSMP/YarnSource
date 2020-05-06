/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class RepairItemRecipe
extends SpecialCraftingRecipe {
    public RepairItemRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = arg.getStack(i);
            if (lv.isEmpty()) continue;
            list.add(lv);
            if (list.size() <= 1) continue;
            ItemStack lv2 = (ItemStack)list.get(0);
            if (lv.getItem() == lv2.getItem() && lv2.getCount() == 1 && lv.getCount() == 1 && lv2.getItem().isDamageable()) continue;
            return false;
        }
        return list.size() == 2;
    }

    @Override
    public ItemStack craft(CraftingInventory arg2) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < arg2.size(); ++i) {
            ItemStack lv = arg2.getStack(i);
            if (lv.isEmpty()) continue;
            list.add(lv);
            if (list.size() <= 1) continue;
            ItemStack lv2 = (ItemStack)list.get(0);
            if (lv.getItem() == lv2.getItem() && lv2.getCount() == 1 && lv.getCount() == 1 && lv2.getItem().isDamageable()) continue;
            return ItemStack.EMPTY;
        }
        if (list.size() == 2) {
            ItemStack lv3 = (ItemStack)list.get(0);
            ItemStack lv4 = (ItemStack)list.get(1);
            if (lv3.getItem() == lv4.getItem() && lv3.getCount() == 1 && lv4.getCount() == 1 && lv3.getItem().isDamageable()) {
                Item lv5 = lv3.getItem();
                int j = lv5.getMaxDamage() - lv3.getDamage();
                int k = lv5.getMaxDamage() - lv4.getDamage();
                int l = j + k + lv5.getMaxDamage() * 5 / 100;
                int m = lv5.getMaxDamage() - l;
                if (m < 0) {
                    m = 0;
                }
                ItemStack lv6 = new ItemStack(lv3.getItem());
                lv6.setDamage(m);
                HashMap map = Maps.newHashMap();
                Map<Enchantment, Integer> map2 = EnchantmentHelper.get(lv3);
                Map<Enchantment, Integer> map3 = EnchantmentHelper.get(lv4);
                Registry.ENCHANTMENT.stream().filter(Enchantment::isCursed).forEach(arg -> {
                    int i = Math.max(map2.getOrDefault(arg, 0), map3.getOrDefault(arg, 0));
                    if (i > 0) {
                        map.put(arg, i);
                    }
                });
                if (!map.isEmpty()) {
                    EnchantmentHelper.set(map, lv6);
                }
                return lv6;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}

