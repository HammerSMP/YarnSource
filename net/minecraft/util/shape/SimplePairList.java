/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.shape.PairList;

public final class SimplePairList
implements PairList {
    private final DoubleArrayList valueIndices;
    private final IntArrayList minValues;
    private final IntArrayList maxValues;

    protected SimplePairList(DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
        int i = 0;
        int j = 0;
        double d = Double.NaN;
        int k = doubleList.size();
        int l = doubleList2.size();
        int m = k + l;
        this.valueIndices = new DoubleArrayList(m);
        this.minValues = new IntArrayList(m);
        this.maxValues = new IntArrayList(m);
        do {
            double e;
            boolean bl4;
            boolean bl3 = i < k;
            boolean bl5 = bl4 = j < l;
            if (!bl3 && !bl4) break;
            boolean bl52 = bl3 && (!bl4 || doubleList.getDouble(i) < doubleList2.getDouble(j) + 1.0E-7);
            double d2 = e = bl52 ? doubleList.getDouble(i++) : doubleList2.getDouble(j++);
            if ((i == 0 || !bl3) && !bl52 && !bl2 || (j == 0 || !bl4) && bl52 && !bl) continue;
            if (!(d >= e - 1.0E-7)) {
                this.minValues.add(i - 1);
                this.maxValues.add(j - 1);
                this.valueIndices.add(e);
                d = e;
                continue;
            }
            if (this.valueIndices.isEmpty()) continue;
            this.minValues.set(this.minValues.size() - 1, i - 1);
            this.maxValues.set(this.maxValues.size() - 1, j - 1);
        } while (true);
        if (this.valueIndices.isEmpty()) {
            this.valueIndices.add(Math.min(doubleList.getDouble(k - 1), doubleList2.getDouble(l - 1)));
        }
    }

    @Override
    public boolean forEachPair(PairList.Consumer arg) {
        for (int i = 0; i < this.valueIndices.size() - 1; ++i) {
            if (arg.merge(this.minValues.getInt(i), this.maxValues.getInt(i), i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getPairs() {
        return this.valueIndices;
    }
}

