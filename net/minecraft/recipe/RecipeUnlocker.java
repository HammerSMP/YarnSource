/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.recipe;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface RecipeUnlocker {
    public void setLastRecipe(@Nullable Recipe<?> var1);

    @Nullable
    public Recipe<?> getLastRecipe();

    default public void unlockLastRecipe(PlayerEntity arg) {
        Recipe<?> lv = this.getLastRecipe();
        if (lv != null && !lv.isIgnoredInRecipeBook()) {
            arg.unlockRecipes(Collections.singleton(lv));
            this.setLastRecipe(null);
        }
    }

    default public boolean shouldCraftRecipe(World arg, ServerPlayerEntity arg2, Recipe<?> arg3) {
        if (arg3.isIgnoredInRecipeBook() || !arg.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) || arg2.getRecipeBook().contains(arg3)) {
            this.setLastRecipe(arg3);
            return true;
        }
        return false;
    }
}

