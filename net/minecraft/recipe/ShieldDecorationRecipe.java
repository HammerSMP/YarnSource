/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShieldDecorationRecipe
extends SpecialCraftingRecipe {
    public ShieldDecorationRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        ItemStack lv = ItemStack.EMPTY;
        ItemStack lv2 = ItemStack.EMPTY;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv3 = arg.getStack(i);
            if (lv3.isEmpty()) continue;
            if (lv3.getItem() instanceof BannerItem) {
                if (!lv2.isEmpty()) {
                    return false;
                }
                lv2 = lv3;
                continue;
            }
            if (lv3.getItem() == Items.SHIELD) {
                if (!lv.isEmpty()) {
                    return false;
                }
                if (lv3.getSubTag("BlockEntityTag") != null) {
                    return false;
                }
                lv = lv3;
                continue;
            }
            return false;
        }
        return !lv.isEmpty() && !lv2.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ItemStack lv = ItemStack.EMPTY;
        ItemStack lv2 = ItemStack.EMPTY;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv3 = arg.getStack(i);
            if (lv3.isEmpty()) continue;
            if (lv3.getItem() instanceof BannerItem) {
                lv = lv3;
                continue;
            }
            if (lv3.getItem() != Items.SHIELD) continue;
            lv2 = lv3.copy();
        }
        if (lv2.isEmpty()) {
            return lv2;
        }
        CompoundTag lv4 = lv.getSubTag("BlockEntityTag");
        CompoundTag lv5 = lv4 == null ? new CompoundTag() : lv4.copy();
        lv5.putInt("Base", ((BannerItem)lv.getItem()).getColor().getId());
        lv2.putSubTag("BlockEntityTag", lv5);
        return lv2;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}

