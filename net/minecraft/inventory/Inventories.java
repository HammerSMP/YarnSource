/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.inventory;

import java.util.List;
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
}

