/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.collection.DefaultedList;

public class BasicInventory
implements Inventory,
RecipeInputProvider {
    private final int size;
    private final DefaultedList<ItemStack> stacks;
    private List<InventoryChangedListener> listeners;

    public BasicInventory(int i) {
        this.size = i;
        this.stacks = DefaultedList.ofSize(i, ItemStack.EMPTY);
    }

    public BasicInventory(ItemStack ... args) {
        this.size = args.length;
        this.stacks = DefaultedList.copyOf(ItemStack.EMPTY, args);
    }

    public void addListener(InventoryChangedListener arg) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add(arg);
    }

    public void removeListener(InventoryChangedListener arg) {
        this.listeners.remove(arg);
    }

    @Override
    public ItemStack getStack(int i) {
        if (i < 0 || i >= this.stacks.size()) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(i);
    }

    public List<ItemStack> clearToList() {
        List<ItemStack> list = this.stacks.stream().filter(arg -> !arg.isEmpty()).collect(Collectors.toList());
        this.clear();
        return list;
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        ItemStack lv = Inventories.splitStack(this.stacks, i, j);
        if (!lv.isEmpty()) {
            this.markDirty();
        }
        return lv;
    }

    public ItemStack removeItem(Item arg, int i) {
        ItemStack lv = new ItemStack(arg, 0);
        for (int j = this.size - 1; j >= 0; --j) {
            ItemStack lv2 = this.getStack(j);
            if (!lv2.getItem().equals(arg)) continue;
            int k = i - lv.getCount();
            ItemStack lv3 = lv2.split(k);
            lv.increment(lv3.getCount());
            if (lv.getCount() == i) break;
        }
        if (!lv.isEmpty()) {
            this.markDirty();
        }
        return lv;
    }

    public ItemStack addStack(ItemStack arg) {
        ItemStack lv = arg.copy();
        this.addToExistingSlot(lv);
        if (lv.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.addToNewSlot(lv);
        if (lv.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return lv;
    }

    public boolean canInsert(ItemStack arg) {
        boolean bl = false;
        for (ItemStack lv : this.stacks) {
            if (!lv.isEmpty() && (!this.canCombine(lv, arg) || lv.getCount() >= lv.getMaxCount())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    @Override
    public ItemStack removeStack(int i) {
        ItemStack lv = this.stacks.get(i);
        if (lv.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.stacks.set(i, ItemStack.EMPTY);
        return lv;
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        this.stacks.set(i, arg);
        if (!arg.isEmpty() && arg.getCount() > this.getMaxCountPerStack()) {
            arg.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public int size() {
        return this.size;
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
    public void markDirty() {
        if (this.listeners != null) {
            for (InventoryChangedListener lv : this.listeners) {
                lv.onInventoryChanged(this);
            }
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
        this.markDirty();
    }

    @Override
    public void provideRecipeInputs(RecipeFinder arg) {
        for (ItemStack lv : this.stacks) {
            arg.addItem(lv);
        }
    }

    public String toString() {
        return this.stacks.stream().filter(arg -> !arg.isEmpty()).collect(Collectors.toList()).toString();
    }

    private void addToNewSlot(ItemStack arg) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack lv = this.getStack(i);
            if (!lv.isEmpty()) continue;
            this.setStack(i, arg.copy());
            arg.setCount(0);
            return;
        }
    }

    private void addToExistingSlot(ItemStack arg) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack lv = this.getStack(i);
            if (!this.canCombine(lv, arg)) continue;
            this.transfer(arg, lv);
            if (!arg.isEmpty()) continue;
            return;
        }
    }

    private boolean canCombine(ItemStack arg, ItemStack arg2) {
        return arg.getItem() == arg2.getItem() && ItemStack.areTagsEqual(arg, arg2);
    }

    private void transfer(ItemStack arg, ItemStack arg2) {
        int i = Math.min(this.getMaxCountPerStack(), arg2.getMaxCount());
        int j = Math.min(arg.getCount(), i - arg2.getCount());
        if (j > 0) {
            arg2.increment(j);
            arg.decrement(j);
            this.markDirty();
        }
    }

    public void readTags(ListTag arg) {
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = ItemStack.fromTag(arg.getCompound(i));
            if (lv.isEmpty()) continue;
            this.addStack(lv);
        }
    }

    public ListTag getTags() {
        ListTag lv = new ListTag();
        for (int i = 0; i < this.size(); ++i) {
            ItemStack lv2 = this.getStack(i);
            if (lv2.isEmpty()) continue;
            lv.add(lv2.toTag(new CompoundTag()));
        }
        return lv;
    }
}

