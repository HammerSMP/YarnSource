/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public interface Inventory
extends Clearable {
    public int size();

    public boolean isEmpty();

    public ItemStack getStack(int var1);

    public ItemStack removeStack(int var1, int var2);

    public ItemStack removeStack(int var1);

    public void setStack(int var1, ItemStack var2);

    default public int getMaxCountPerStack() {
        return 64;
    }

    public void markDirty();

    public boolean canPlayerUse(PlayerEntity var1);

    default public void onOpen(PlayerEntity arg) {
    }

    default public void onClose(PlayerEntity arg) {
    }

    default public boolean isValid(int i, ItemStack arg) {
        return true;
    }

    default public int count(Item arg) {
        int i = 0;
        for (int j = 0; j < this.size(); ++j) {
            ItemStack lv = this.getStack(j);
            if (!lv.getItem().equals(arg)) continue;
            i += lv.getCount();
        }
        return i;
    }

    default public boolean containsAny(Set<Item> set) {
        for (int i = 0; i < this.size(); ++i) {
            ItemStack lv = this.getStack(i);
            if (!set.contains(lv.getItem()) || lv.getCount() <= 0) continue;
            return true;
        }
        return false;
    }
}

