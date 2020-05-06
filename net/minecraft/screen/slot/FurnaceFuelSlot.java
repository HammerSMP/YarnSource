/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FurnaceFuelSlot
extends Slot {
    private final AbstractFurnaceScreenHandler handler;

    public FurnaceFuelSlot(AbstractFurnaceScreenHandler arg, Inventory arg2, int i, int j, int k) {
        super(arg2, i, j, k);
        this.handler = arg;
    }

    @Override
    public boolean canInsert(ItemStack arg) {
        return this.handler.isFuel(arg) || FurnaceFuelSlot.isBucket(arg);
    }

    @Override
    public int getMaxStackAmount(ItemStack arg) {
        return FurnaceFuelSlot.isBucket(arg) ? 1 : super.getMaxStackAmount(arg);
    }

    public static boolean isBucket(ItemStack arg) {
        return arg.getItem() == Items.BUCKET;
    }
}

