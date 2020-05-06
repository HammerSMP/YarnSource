/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MapExtendingRecipe
extends ShapedRecipe {
    public MapExtendingRecipe(Identifier arg) {
        super(arg, "", 3, 3, DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.FILLED_MAP), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER)), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        if (!super.matches(arg, arg2)) {
            return false;
        }
        ItemStack lv = ItemStack.EMPTY;
        for (int i = 0; i < arg.size() && lv.isEmpty(); ++i) {
            ItemStack lv2 = arg.getStack(i);
            if (lv2.getItem() != Items.FILLED_MAP) continue;
            lv = lv2;
        }
        if (lv.isEmpty()) {
            return false;
        }
        MapState lv3 = FilledMapItem.getOrCreateMapState(lv, arg2);
        if (lv3 == null) {
            return false;
        }
        if (this.matches(lv3)) {
            return false;
        }
        return lv3.scale < 4;
    }

    private boolean matches(MapState arg) {
        if (arg.icons != null) {
            for (MapIcon lv : arg.icons.values()) {
                if (lv.getType() != MapIcon.Type.MANSION && lv.getType() != MapIcon.Type.MONUMENT) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ItemStack lv = ItemStack.EMPTY;
        for (int i = 0; i < arg.size() && lv.isEmpty(); ++i) {
            ItemStack lv2 = arg.getStack(i);
            if (lv2.getItem() != Items.FILLED_MAP) continue;
            lv = lv2;
        }
        lv = lv.copy();
        lv.setCount(1);
        lv.getOrCreateTag().putInt("map_scale_direction", 1);
        return lv;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}

