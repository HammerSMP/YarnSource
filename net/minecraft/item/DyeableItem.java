/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public interface DyeableItem {
    default public boolean hasColor(ItemStack stack) {
        CompoundTag lv = stack.getSubTag("display");
        return lv != null && lv.contains("color", 99);
    }

    default public int getColor(ItemStack stack) {
        CompoundTag lv = stack.getSubTag("display");
        if (lv != null && lv.contains("color", 99)) {
            return lv.getInt("color");
        }
        return 10511680;
    }

    default public void removeColor(ItemStack stack) {
        CompoundTag lv = stack.getSubTag("display");
        if (lv != null && lv.contains("color")) {
            lv.remove("color");
        }
    }

    default public void setColor(ItemStack stack, int color) {
        stack.getOrCreateSubTag("display").putInt("color", color);
    }

    public static ItemStack blendAndSetColor(ItemStack stack, List<DyeItem> colors) {
        ItemStack lv = ItemStack.EMPTY;
        int[] is = new int[3];
        int i = 0;
        int j = 0;
        DyeableItem lv2 = null;
        Item lv3 = stack.getItem();
        if (lv3 instanceof DyeableItem) {
            lv2 = (DyeableItem)((Object)lv3);
            lv = stack.copy();
            lv.setCount(1);
            if (lv2.hasColor(stack)) {
                int k = lv2.getColor(lv);
                float f = (float)(k >> 16 & 0xFF) / 255.0f;
                float g = (float)(k >> 8 & 0xFF) / 255.0f;
                float h = (float)(k & 0xFF) / 255.0f;
                i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0f);
                is[0] = (int)((float)is[0] + f * 255.0f);
                is[1] = (int)((float)is[1] + g * 255.0f);
                is[2] = (int)((float)is[2] + h * 255.0f);
                ++j;
            }
            for (DyeItem lv4 : colors) {
                float[] fs = lv4.getColor().getColorComponents();
                int l = (int)(fs[0] * 255.0f);
                int m = (int)(fs[1] * 255.0f);
                int n = (int)(fs[2] * 255.0f);
                i += Math.max(l, Math.max(m, n));
                is[0] = is[0] + l;
                is[1] = is[1] + m;
                is[2] = is[2] + n;
                ++j;
            }
        }
        if (lv2 == null) {
            return ItemStack.EMPTY;
        }
        int o = is[0] / j;
        int p = is[1] / j;
        int q = is[2] / j;
        float r = (float)i / (float)j;
        float s = Math.max(o, Math.max(p, q));
        o = (int)((float)o * r / s);
        p = (int)((float)p * r / s);
        q = (int)((float)q * r / s);
        int t = o;
        t = (t << 8) + p;
        t = (t << 8) + q;
        lv2.setColor(lv, t);
        return lv;
    }
}

