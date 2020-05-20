/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BlastFurnaceBlockEntity
extends AbstractFurnaceBlockEntity {
    public BlastFurnaceBlockEntity() {
        super(BlockEntityType.BLAST_FURNACE, RecipeType.BLASTING);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.blast_furnace");
    }

    @Override
    protected int getFuelTime(ItemStack arg) {
        return super.getFuelTime(arg) / 2;
    }

    @Override
    protected ScreenHandler createScreenHandler(int i, PlayerInventory arg) {
        return new BlastFurnaceScreenHandler(i, arg, this, this.propertyDelegate);
    }
}
