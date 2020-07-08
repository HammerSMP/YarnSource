/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractRecipeScreenHandler<C extends Inventory>
extends ScreenHandler {
    public AbstractRecipeScreenHandler(ScreenHandlerType<?> arg, int i) {
        super(arg, i);
    }

    public void fillInputSlots(boolean bl, Recipe<?> arg, ServerPlayerEntity arg2) {
        new InputSlotFiller(this).fillInputSlots(arg2, arg, bl);
    }

    public abstract void populateRecipeFinder(RecipeFinder var1);

    public abstract void clearCraftingSlots();

    public abstract boolean matches(Recipe<? super C> var1);

    public abstract int getCraftingResultSlotIndex();

    public abstract int getCraftingWidth();

    public abstract int getCraftingHeight();

    @Environment(value=EnvType.CLIENT)
    public abstract int getCraftingSlotCount();

    @Environment(value=EnvType.CLIENT)
    public abstract RecipeBookCategory getCategory();
}

