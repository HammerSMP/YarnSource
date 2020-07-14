/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen.slot;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ShulkerBoxSlot
extends Slot {
    public ShulkerBoxSlot(Inventory arg, int i, int j, int k) {
        super(arg, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
}

