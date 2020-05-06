/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface SidedInventory
extends Inventory {
    public int[] getAvailableSlots(Direction var1);

    public boolean canInsert(int var1, ItemStack var2, @Nullable Direction var3);

    public boolean canExtract(int var1, ItemStack var2, Direction var3);
}

