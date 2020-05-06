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

    public CraftingInventory(ScreenHandler arg, int i, int j) {
        this.stacks = DefaultedList.ofSize(i * j, ItemStack.EMPTY);
        this.handler = arg;
        this.width = i;
        this.height = j;
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
    public ItemStack getStack(int i) {
        if (i >= this.size()) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(i);
    }

    @Override
    public ItemStack removeStack(int i) {
        return Inventories.removeStack(this.stacks, i);
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        ItemStack lv = Inventories.splitStack(this.stacks, i, j);
        if (!lv.isEmpty()) {
            this.handler.onContentChanged(this);
        }
        return lv;
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        this.stacks.set(i, arg);
        this.handler.onContentChanged(this);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
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
    public void provideRecipeInputs(RecipeFinder arg) {
        for (ItemStack lv : this.stacks) {
            arg.addNormalItem(lv);
        }
    }
}

