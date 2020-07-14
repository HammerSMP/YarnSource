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
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe
extends SpecialCraftingRecipe {
    public ShulkerBoxColoringRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        int i = 0;
        int j = 0;
        for (int k = 0; k < arg.size(); ++k) {
            ItemStack lv = arg.getStack(k);
            if (lv.isEmpty()) continue;
            if (Block.getBlockFromItem(lv.getItem()) instanceof ShulkerBoxBlock) {
                ++i;
            } else if (lv.getItem() instanceof DyeItem) {
                ++j;
            } else {
                return false;
            }
            if (j <= 1 && i <= 1) continue;
            return false;
        }
        return i == 1 && j == 1;
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ItemStack lv = ItemStack.EMPTY;
        DyeItem lv2 = (DyeItem)Items.WHITE_DYE;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv3 = arg.getStack(i);
            if (lv3.isEmpty()) continue;
            Item lv4 = lv3.getItem();
            if (Block.getBlockFromItem(lv4) instanceof ShulkerBoxBlock) {
                lv = lv3;
                continue;
            }
            if (!(lv4 instanceof DyeItem)) continue;
            lv2 = (DyeItem)lv4;
        }
        ItemStack lv5 = ShulkerBoxBlock.getItemStack(lv2.getColor());
        if (lv.hasTag()) {
            lv5.setTag(lv.getTag().copy());
        }
        return lv5;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHULKER_BOX;
    }
}

