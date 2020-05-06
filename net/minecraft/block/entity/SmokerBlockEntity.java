/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SmokerBlockEntity
extends AbstractFurnaceBlockEntity {
    public SmokerBlockEntity() {
        super(BlockEntityType.SMOKER, RecipeType.SMOKING);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.smoker");
    }

    @Override
    protected int getFuelTime(ItemStack arg) {
        return super.getFuelTime(arg) / 2;
    }

    @Override
    protected ScreenHandler createContainer(int i, PlayerInventory arg) {
        return new SmokerScreenHandler(i, arg, this, this.propertyDelegate);
    }
}

