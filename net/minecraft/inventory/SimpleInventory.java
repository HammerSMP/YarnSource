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

public class SimpleInventory
implements Inventory,
RecipeInputProvider {
    private final int size;
    private final DefaultedList<ItemStack> stacks;
    private List<InventoryChangedListener> listeners;

    public SimpleInventory(int size) {
        this.size = size;
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public SimpleInventory(ItemStack ... items) {
        this.size = items.length;
        this.stacks = DefaultedList.copyOf(ItemStack.EMPTY, items);
    }

    public void addListener(InventoryChangedListener listener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add(listener);
    }

    public void removeListener(InventoryChangedListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(slot);
    }

    public List<ItemStack> clearToList() {
        List<ItemStack> list = this.stacks.stream().filter(arg -> !arg.isEmpty()).collect(Collectors.toList());
        this.clear();
        return list;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack lv = Inventories.splitStack(this.stacks, slot, amount);
        if (!lv.isEmpty()) {
            this.markDirty();
        }
        return lv;
    }

    public ItemStack removeItem(Item item, int count) {
        ItemStack lv = new ItemStack(item, 0);
        for (int j = this.size - 1; j >= 0; --j) {
            ItemStack lv2 = this.getStack(j);
            if (!lv2.getItem().equals(item)) continue;
            int k = count - lv.getCount();
            ItemStack lv3 = lv2.split(k);
            lv.increment(lv3.getCount());
            if (lv.getCount() == count) break;
        }
        if (!lv.isEmpty()) {
            this.markDirty();
        }
        return lv;
    }

    public ItemStack addStack(ItemStack stack) {
        ItemStack lv = stack.copy();
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

    public boolean canInsert(ItemStack stack) {
        boolean bl = false;
        for (ItemStack lv : this.stacks) {
            if (!lv.isEmpty() && (!this.canCombine(lv, stack) || lv.getCount() >= lv.getMaxCount())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack lv = this.stacks.get(slot);
        if (lv.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.stacks.set(slot, ItemStack.EMPTY);
        return lv;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
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
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
        this.markDirty();
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack lv : this.stacks) {
            finder.addItem(lv);
        }
    }

    public String toString() {
        return this.stacks.stream().filter(arg -> !arg.isEmpty()).collect(Collectors.toList()).toString();
    }

    private void addToNewSlot(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack lv = this.getStack(i);
            if (!lv.isEmpty()) continue;
            this.setStack(i, stack.copy());
            stack.setCount(0);
            return;
        }
    }

    private void addToExistingSlot(ItemStack stack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack lv = this.getStack(i);
            if (!this.canCombine(lv, stack)) continue;
            this.transfer(stack, lv);
            if (!stack.isEmpty()) continue;
            return;
        }
    }

    private boolean canCombine(ItemStack one, ItemStack two) {
        return one.getItem() == two.getItem() && ItemStack.areTagsEqual(one, two);
    }

    private void transfer(ItemStack source, ItemStack target) {
        int i = Math.min(this.getMaxCountPerStack(), target.getMaxCount());
        int j = Math.min(source.getCount(), i - target.getCount());
        if (j > 0) {
            target.increment(j);
            source.decrement(j);
            this.markDirty();
        }
    }

    public void readTags(ListTag tags) {
        for (int i = 0; i < tags.size(); ++i) {
            ItemStack lv = ItemStack.fromTag(tags.getCompound(i));
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

