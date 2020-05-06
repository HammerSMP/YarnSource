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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe
extends SpecialCraftingRecipe {
    private static final Ingredient INPUT_STAR = Ingredient.ofItems(Items.FIREWORK_STAR);

    public FireworkStarFadeRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = arg.getStack(i);
            if (lv.isEmpty()) continue;
            if (lv.getItem() instanceof DyeItem) {
                bl = true;
                continue;
            }
            if (INPUT_STAR.test(lv)) {
                if (bl2) {
                    return false;
                }
                bl2 = true;
                continue;
            }
            return false;
        }
        return bl2 && bl;
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ArrayList list = Lists.newArrayList();
        ItemStack lv = null;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv2 = arg.getStack(i);
            Item lv3 = lv2.getItem();
            if (lv3 instanceof DyeItem) {
                list.add(((DyeItem)lv3).getColor().getFireworkColor());
                continue;
            }
            if (!INPUT_STAR.test(lv2)) continue;
            lv = lv2.copy();
            lv.setCount(1);
        }
        if (lv == null || list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        lv.getOrCreateSubTag("Explosion").putIntArray("FadeColors", list);
        return lv;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }
}

