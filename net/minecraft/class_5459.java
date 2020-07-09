/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class class_5459 {
    public static class_5460 method_30574(BlockPos arg, Direction.Axis arg2, int i, Direction.Axis arg3, int j, Predicate<BlockPos> predicate) {
        BlockPos.Mutable lv = arg.mutableCopy();
        Direction lv2 = Direction.get(Direction.AxisDirection.NEGATIVE, arg2);
        Direction lv3 = lv2.getOpposite();
        Direction lv4 = Direction.get(Direction.AxisDirection.NEGATIVE, arg3);
        Direction lv5 = lv4.getOpposite();
        int k = class_5459.method_30575(predicate, lv.set(arg), lv2, i);
        int l = class_5459.method_30575(predicate, lv.set(arg), lv3, i);
        int m = k;
        class_5461[] lvs = new class_5461[m + 1 + l];
        lvs[m] = new class_5461(class_5459.method_30575(predicate, lv.set(arg), lv4, j), class_5459.method_30575(predicate, lv.set(arg), lv5, j));
        int n = lvs[m].field_25939;
        for (int o = 1; o <= k; ++o) {
            class_5461 lv6 = lvs[m - (o - 1)];
            lvs[m - o] = new class_5461(class_5459.method_30575(predicate, lv.set(arg).move(lv2, o), lv4, lv6.field_25939), class_5459.method_30575(predicate, lv.set(arg).move(lv2, o), lv5, lv6.field_25940));
        }
        for (int p = 1; p <= l; ++p) {
            class_5461 lv7 = lvs[m + p - 1];
            lvs[m + p] = new class_5461(class_5459.method_30575(predicate, lv.set(arg).move(lv3, p), lv4, lv7.field_25939), class_5459.method_30575(predicate, lv.set(arg).move(lv3, p), lv5, lv7.field_25940));
        }
        int q = 0;
        int r = 0;
        int s = 0;
        int t = 0;
        int[] is = new int[lvs.length];
        for (int u = n; u >= 0; --u) {
            for (int v = 0; v < lvs.length; ++v) {
                class_5461 lv8 = lvs[v];
                int w = n - lv8.field_25939;
                int x = n + lv8.field_25940;
                is[v] = u >= w && u <= x ? x + 1 - u : 0;
            }
            Pair<class_5461, Integer> pair = class_5459.method_30576(is);
            class_5461 lv9 = (class_5461)pair.getFirst();
            int y = 1 + lv9.field_25940 - lv9.field_25939;
            int z = (Integer)pair.getSecond();
            if (y * z <= s * t) continue;
            q = lv9.field_25939;
            r = u;
            s = y;
            t = z;
        }
        return new class_5460(arg.method_30513(arg2, q - m).method_30513(arg3, r - n), s, t);
    }

    private static int method_30575(Predicate<BlockPos> predicate, BlockPos.Mutable arg, Direction arg2, int i) {
        int j;
        for (j = 0; j < i && predicate.test(arg.move(arg2)); ++j) {
        }
        return j;
    }

    @VisibleForTesting
    static Pair<class_5461, Integer> method_30576(int[] is) {
        int i = 0;
        int j = 0;
        int k = 0;
        IntArrayList intStack = new IntArrayList();
        intStack.push(0);
        for (int l = 1; l <= is.length; ++l) {
            int m;
            int n = m = l == is.length ? 0 : is[l];
            while (!intStack.isEmpty()) {
                int n2 = is[intStack.topInt()];
                if (m >= n2) {
                    intStack.push(l);
                    break;
                }
                intStack.popInt();
                int o = intStack.isEmpty() ? 0 : intStack.topInt() + 1;
                if (n2 * (l - o) <= k * (j - i)) continue;
                j = l;
                i = o;
                k = n2;
            }
            if (!intStack.isEmpty()) continue;
            intStack.push(l);
        }
        return new Pair((Object)new class_5461(i, j - 1), (Object)k);
    }

    public static class class_5460 {
        public final BlockPos field_25936;
        public final int field_25937;
        public final int field_25938;

        public class_5460(BlockPos arg, int i, int j) {
            this.field_25936 = arg;
            this.field_25937 = i;
            this.field_25938 = j;
        }
    }

    public static class class_5461 {
        public final int field_25939;
        public final int field_25940;

        public class_5461(int i, int j) {
            this.field_25939 = i;
            this.field_25940 = j;
        }

        public String toString() {
            return "IntBounds{min=" + this.field_25939 + ", max=" + this.field_25940 + '}';
        }
    }
}

