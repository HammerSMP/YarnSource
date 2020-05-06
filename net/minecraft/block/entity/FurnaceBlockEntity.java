/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class FurnaceBlockEntity
extends AbstractFurnaceBlockEntity {
    public FurnaceBlockEntity() {
        super(BlockEntityType.FURNACE, RecipeType.SMELTING);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.furnace");
    }

    @Override
    protected ScreenHandler createContainer(int i, PlayerInventory arg) {
        return new FurnaceScreenHandler(i, arg, this, this.propertyDelegate);
    }
}

