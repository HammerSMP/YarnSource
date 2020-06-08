/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public interface RecipeType<T extends Recipe<?>> {
    public static final RecipeType<CraftingRecipe> CRAFTING = RecipeType.register("crafting");
    public static final RecipeType<SmeltingRecipe> SMELTING = RecipeType.register("smelting");
    public static final RecipeType<BlastingRecipe> BLASTING = RecipeType.register("blasting");
    public static final RecipeType<SmokingRecipe> SMOKING = RecipeType.register("smoking");
    public static final RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeType.register("campfire_cooking");
    public static final RecipeType<StonecuttingRecipe> STONECUTTING = RecipeType.register("stonecutting");
    public static final RecipeType<SmithingRecipe> SMITHING = RecipeType.register("smithing");

    public static <T extends Recipe<?>> RecipeType<T> register(final String string) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(string), new RecipeType<T>(){

            public String toString() {
                return string;
            }
        });
    }

    default public <C extends Inventory> Optional<T> get(Recipe<C> arg, World arg2, C arg3) {
        return arg.matches(arg3, arg2) ? Optional.of(arg) : Optional.empty();
    }
}

