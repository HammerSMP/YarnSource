/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 */
package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FurnaceInputSlotFiller<C extends Inventory>
extends InputSlotFiller<C> {
    private boolean slotMatchesRecipe;

    public FurnaceInputSlotFiller(AbstractRecipeScreenHandler<C> arg) {
        super(arg);
    }

    @Override
    protected void fillInputSlots(Recipe<C> arg, boolean bl) {
        ItemStack lv;
        this.slotMatchesRecipe = this.craftingScreenHandler.matches(arg);
        int i = this.recipeFinder.countRecipeCrafts(arg, null);
        if (this.slotMatchesRecipe && ((lv = this.craftingScreenHandler.getSlot(0).getStack()).isEmpty() || i <= lv.getCount())) {
            return;
        }
        IntArrayList intList = new IntArrayList();
        int j = this.getAmountToFill(bl, i, this.slotMatchesRecipe);
        if (!this.recipeFinder.findRecipe(arg, (IntList)intList, j)) {
            return;
        }
        if (!this.slotMatchesRecipe) {
            this.returnSlot(this.craftingScreenHandler.getCraftingResultSlotIndex());
            this.returnSlot(0);
        }
        this.fillInputSlot(j, (IntList)intList);
    }

    @Override
    protected void returnInputs() {
        this.returnSlot(this.craftingScreenHandler.getCraftingResultSlotIndex());
        super.returnInputs();
    }

    protected void fillInputSlot(int i, IntList intList) {
        IntListIterator iterator = intList.iterator();
        Slot lv = this.craftingScreenHandler.getSlot(0);
        ItemStack lv2 = RecipeFinder.getStackFromId((Integer)iterator.next());
        if (lv2.isEmpty()) {
            return;
        }
        int j = Math.min(lv2.getMaxCount(), i);
        if (this.slotMatchesRecipe) {
            j -= lv.getStack().getCount();
        }
        for (int k = 0; k < j; ++k) {
            this.fillInputSlot(lv, lv2);
        }
    }
}

