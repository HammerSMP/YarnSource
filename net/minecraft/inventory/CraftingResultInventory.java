/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.collection.DefaultedList;

public class CraftingResultInventory
implements Inventory,
RecipeUnlocker {
    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private Recipe<?> lastRecipe;

    @Override
    public int size() {
        return 1;
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
        return this.stacks.get(0);
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        return Inventories.removeStack(this.stacks, 0);
    }

    @Override
    public ItemStack removeStack(int i) {
        return Inventories.removeStack(this.stacks, 0);
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        this.stacks.set(0, arg);
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

    @Override
    public void setLastRecipe(@Nullable Recipe<?> arg) {
        this.lastRecipe = arg;
    }

    @Override
    @Nullable
    public Recipe<?> getLastRecipe() {
        return this.lastRecipe;
    }
}

