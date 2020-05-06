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
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StonecuttingRecipe
extends CuttingRecipe {
    public StonecuttingRecipe(Identifier arg, String string, Ingredient arg2, ItemStack arg3) {
        super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTING, arg, string, arg2, arg3);
    }

    @Override
    public boolean matches(Inventory arg, World arg2) {
        return this.input.test(arg.getStack(0));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(Blocks.STONECUTTER);
    }
}

