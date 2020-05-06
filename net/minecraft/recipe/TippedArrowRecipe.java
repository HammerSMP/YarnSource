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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TippedArrowRecipe
extends SpecialCraftingRecipe {
    public TippedArrowRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        if (arg.getWidth() != 3 || arg.getHeight() != 3) {
            return false;
        }
        for (int i = 0; i < arg.getWidth(); ++i) {
            for (int j = 0; j < arg.getHeight(); ++j) {
                ItemStack lv = arg.getStack(i + j * arg.getWidth());
                if (lv.isEmpty()) {
                    return false;
                }
                Item lv2 = lv.getItem();
                if (!(i == 1 && j == 1 ? lv2 != Items.LINGERING_POTION : lv2 != Items.ARROW)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ItemStack lv = arg.getStack(1 + arg.getWidth());
        if (lv.getItem() != Items.LINGERING_POTION) {
            return ItemStack.EMPTY;
        }
        ItemStack lv2 = new ItemStack(Items.TIPPED_ARROW, 8);
        PotionUtil.setPotion(lv2, PotionUtil.getPotion(lv));
        PotionUtil.setCustomPotionEffects(lv2, PotionUtil.getCustomPotionEffects(lv));
        return lv2;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i >= 2 && j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}

