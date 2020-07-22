/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.collection;

import java.util.List;
import java.util.Random;
import net.minecraft.util.Util;

public class WeightedPicker {
    public static int getWeightSum(List<? extends Entry> list) {
        int i = 0;
        int k = list.size();
        for (int j = 0; j < k; ++j) {
            Entry lv = list.get(j);
            i += lv.weight;
        }
        return i;
    }

    public static <T extends Entry> T getRandom(Random random, List<T> list, int weightSum) {
        if (weightSum <= 0) {
            throw Util.throwOrPause(new IllegalArgumentException());
        }
        int j = random.nextInt(weightSum);
        return WeightedPicker.getAt(list, j);
    }

    public static <T extends Entry> T getAt(List<T> list, int weightMark) {
        int k = list.size();
        for (int j = 0; j < k; ++j) {
            Entry lv = (Entry)list.get(j);
            if ((weightMark -= lv.weight) >= 0) continue;
            return (T)lv;
        }
        return null;
    }

    public static <T extends Entry> T getRandom(Random random, List<T> list) {
        return WeightedPicker.getRandom(random, list, WeightedPicker.getWeightSum(list));
    }

    public static class Entry {
        protected final int weight;

        public Entry(int i) {
            this.weight = i;
        }
    }
}

