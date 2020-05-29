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
    public static ItemStack splitStack(List<ItemStack> list, int i, int j) {
        if (i < 0 || i >= list.size() || list.get(i).isEmpty() || j <= 0) {
            return ItemStack.EMPTY;
        }
        return list.get(i).split(j);
    }

    public static ItemStack removeStack(List<ItemStack> list, int i) {
        if (i < 0 || i >= list.size()) {
            return ItemStack.EMPTY;
        }
        return list.set(i, ItemStack.EMPTY);
    }

    public static CompoundTag toTag(CompoundTag arg, DefaultedList<ItemStack> arg2) {
        return Inventories.toTag(arg, arg2, true);
    }

    public static CompoundTag toTag(CompoundTag arg, DefaultedList<ItemStack> arg2, boolean bl) {
        ListTag lv = new ListTag();
        for (int i = 0; i < arg2.size(); ++i) {
            ItemStack lv2 = arg2.get(i);
            if (lv2.isEmpty()) continue;
            CompoundTag lv3 = new CompoundTag();
            lv3.putByte("Slot", (byte)i);
            lv2.toTag(lv3);
            lv.add(lv3);
        }
        if (!lv.isEmpty() || bl) {
            arg.put("Items", lv);
        }
        return arg;
    }

    public static void fromTag(CompoundTag arg, DefaultedList<ItemStack> arg2) {
        ListTag lv = arg.getList("Items", 10);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv2 = lv.getCompound(i);
            int j = lv2.getByte("Slot") & 0xFF;
            if (j < 0 || j >= arg2.size()) continue;
            arg2.set(j, ItemStack.fromTag(lv2));
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

