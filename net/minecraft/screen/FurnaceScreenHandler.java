/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class FurnaceScreenHandler
extends AbstractFurnaceScreenHandler {
    public FurnaceScreenHandler(int i, PlayerInventory arg) {
        super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, i, arg);
    }

    public FurnaceScreenHandler(int i, PlayerInventory arg, Inventory arg2, PropertyDelegate arg3) {
        super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, i, arg, arg2, arg3);
    }
}

