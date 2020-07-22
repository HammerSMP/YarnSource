/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public class CraftingInventory
implements Inventory,
RecipeInputProvider {
    private final DefaultedList<ItemStack> stacks;
    private final int width;
    private final int height;
    private final ScreenHandler handler;

    public CraftingInventory(ScreenHandler handler, int width, int height) {
        this.stacks = DefaultedList.ofSize(width * height, ItemStack.EMPTY);
        this.handler = handler;
        this.width = width;
        this.height = height;
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack lv : this.stacks) {
            if (lv.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= this.size()) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.stacks, slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack lv = Inventories.splitStack(this.stacks, slot, amount);
        if (!lv.isEmpty()) {
            this.handler.onContentChanged(this);
        }
        return lv;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
        this.handler.onContentChanged(this);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack lv : this.stacks) {
            finder.addNormalItem(lv);
        }
    }
}

