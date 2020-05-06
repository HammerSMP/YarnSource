/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class DoubleInventory
implements Inventory {
    private final Inventory first;
    private final Inventory second;

    public DoubleInventory(Inventory arg, Inventory arg2) {
        if (arg == null) {
            arg = arg2;
        }
        if (arg2 == null) {
            arg2 = arg;
        }
        this.first = arg;
        this.second = arg2;
    }

    @Override
    public int size() {
        return this.first.size() + this.second.size();
    }

    @Override
    public boolean isEmpty() {
        return this.first.isEmpty() && this.second.isEmpty();
    }

    public boolean isPart(Inventory arg) {
        return this.first == arg || this.second == arg;
    }

    @Override
    public ItemStack getStack(int i) {
        if (i >= this.first.size()) {
            return this.second.getStack(i - this.first.size());
        }
        return this.first.getStack(i);
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        if (i >= this.first.size()) {
            return this.second.removeStack(i - this.first.size(), j);
        }
        return this.first.removeStack(i, j);
    }

    @Override
    public ItemStack removeStack(int i) {
        if (i >= this.first.size()) {
            return this.second.removeStack(i - this.first.size());
        }
        return this.first.removeStack(i);
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        if (i >= this.first.size()) {
            this.second.setStack(i - this.first.size(), arg);
        } else {
            this.first.setStack(i, arg);
        }
    }

    @Override
    public int getMaxCountPerStack() {
        return this.first.getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        this.first.markDirty();
        this.second.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
        return this.first.canPlayerUse(arg) && this.second.canPlayerUse(arg);
    }

    @Override
    public void onOpen(PlayerEntity arg) {
        this.first.onOpen(arg);
        this.second.onOpen(arg);
    }

    @Override
    public void onClose(PlayerEntity arg) {
        this.first.onClose(arg);
        this.second.onClose(arg);
    }

    @Override
    public boolean isValid(int i, ItemStack arg) {
        if (i >= this.first.size()) {
            return this.second.isValid(i - this.first.size(), arg);
        }
        return this.first.isValid(i, arg);
    }

    @Override
    public void clear() {
        this.first.clear();
        this.second.clear();
    }
}

