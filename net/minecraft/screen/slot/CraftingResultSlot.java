/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class CraftingResultSlot
extends Slot {
    private final CraftingInventory input;
    private final PlayerEntity player;
    private int amount;

    public CraftingResultSlot(PlayerEntity player, CraftingInventory input, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
        this.input = input;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getCount());
        }
        return super.takeStack(amount);
    }

    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    @Override
    protected void onTake(int amount) {
        this.amount += amount;
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        if (this.amount > 0) {
            stack.onCraft(this.player.world, this.player, this.amount);
        }
        if (this.inventory instanceof RecipeUnlocker) {
            ((RecipeUnlocker)((Object)this.inventory)).unlockLastRecipe(this.player);
        }
        this.amount = 0;
    }

    @Override
    public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        DefaultedList<ItemStack> lv = player.world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, this.input, player.world);
        for (int i = 0; i < lv.size(); ++i) {
            ItemStack lv2 = this.input.getStack(i);
            ItemStack lv3 = lv.get(i);
            if (!lv2.isEmpty()) {
                this.input.removeStack(i, 1);
                lv2 = this.input.getStack(i);
            }
            if (lv3.isEmpty()) continue;
            if (lv2.isEmpty()) {
                this.input.setStack(i, lv3);
                continue;
            }
            if (ItemStack.areItemsEqualIgnoreDamage(lv2, lv3) && ItemStack.areTagsEqual(lv2, lv3)) {
                lv3.increment(lv2.getCount());
                this.input.setStack(i, lv3);
                continue;
            }
            if (this.player.inventory.insertStack(lv3)) continue;
            this.player.dropItem(lv3, false);
        }
        return stack;
    }
}

