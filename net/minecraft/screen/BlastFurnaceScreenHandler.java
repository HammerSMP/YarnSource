/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class BlastFurnaceScreenHandler
extends AbstractFurnaceScreenHandler {
    public BlastFurnaceScreenHandler(int i, PlayerInventory arg) {
        super(ScreenHandlerType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookCategory.BLAST_FURNACE, i, arg);
    }

    public BlastFurnaceScreenHandler(int i, PlayerInventory arg, Inventory arg2, PropertyDelegate arg3) {
        super(ScreenHandlerType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookCategory.BLAST_FURNACE, i, arg, arg2, arg3);
    }
}

