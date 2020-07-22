/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.inventory;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;

public class Inventories {
    public static ItemStack splitStack(List<ItemStack> stacks, int slot, int amount) {
        if (slot < 0 || slot >= stacks.size() || stacks.get(slot).isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        return stacks.get(slot).split(amount);
    }

    public static ItemStack removeStack(List<ItemStack> stacks, int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            return ItemStack.EMPTY;
        }
        return stacks.set(slot, ItemStack.EMPTY);
    }

    public static CompoundTag toTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
        return Inventories.toTag(tag, stacks, true);
    }

    public static CompoundTag toTag(CompoundTag tag, DefaultedList<ItemStack> stacks, boolean setIfEmpty) {
        ListTag lv = new ListTag();
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack lv2 = stacks.get(i);
            if (lv2.isEmpty()) continue;
            CompoundTag lv3 = new CompoundTag();
            lv3.putByte("Slot", (byte)i);
            lv2.toTag(lv3);
            lv.add(lv3);
        }
        if (!lv.isEmpty() || setIfEmpty) {
            tag.put("Items", lv);
        }
        return tag;
    }

    public static void fromTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
        ListTag lv = tag.getList("Items", 10);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv2 = lv.getCompound(i);
            int j = lv2.getByte("Slot") & 0xFF;
            if (j < 0 || j >= stacks.size()) continue;
            stacks.set(j, ItemStack.fromTag(lv2));
        }
    }

    public static int method_29234(Inventory arg, Predicate<ItemStack> predicate, int i, boolean bl) {
        int j = 0;
        for (int k = 0; k < arg.size(); ++k) {
            ItemStack lv = arg.getStack(k);
            int l = Inventories.method_29235(lv, predicate, i - j, bl);
            if (l > 0 && !bl && lv.isEmpty()) {
                arg.setStack(k, ItemStack.EMPTY);
            }
            j += l;
        }
        return j;
    }

    public static int method_29235(ItemStack arg, Predicate<ItemStack> predicate, int i, boolean bl) {
        if (arg.isEmpty() || !predicate.test(arg)) {
            return 0;
        }
        if (bl) {
            return arg.getCount();
        }
        int j = i < 0 ? arg.getCount() : Math.min(i, arg.getCount());
        arg.decrement(j);
        return j;
    }
}

