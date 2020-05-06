/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ArmorDyeRecipe
extends SpecialCraftingRecipe {
    public ArmorDyeRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        ItemStack lv = ItemStack.EMPTY;
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv2 = arg.getStack(i);
            if (lv2.isEmpty()) continue;
            if (lv2.getItem() instanceof DyeableItem) {
                if (!lv.isEmpty()) {
                    return false;
                }
                lv = lv2;
                continue;
            }
            if (lv2.getItem() instanceof DyeItem) {
                list.add(lv2);
                continue;
            }
            return false;
        }
        return !lv.isEmpty() && !list.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ArrayList list = Lists.newArrayList();
        ItemStack lv = ItemStack.EMPTY;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv2 = arg.getStack(i);
            if (lv2.isEmpty()) continue;
            Item lv3 = lv2.getItem();
            if (lv3 instanceof DyeableItem) {
                if (!lv.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                lv = lv2.copy();
                continue;
            }
            if (lv3 instanceof DyeItem) {
                list.add((DyeItem)lv3);
                continue;
            }
            return ItemStack.EMPTY;
        }
        if (lv.isEmpty() || list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return DyeableItem.blendAndSetColor(lv, list);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}

