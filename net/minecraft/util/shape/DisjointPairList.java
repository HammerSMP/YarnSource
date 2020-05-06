/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.PairList;

public class DisjointPairList
extends AbstractDoubleList
implements PairList {
    private final DoubleList first;
    private final DoubleList second;
    private final boolean inverted;

    public DisjointPairList(DoubleList doubleList, DoubleList doubleList2, boolean bl) {
        this.first = doubleList;
        this.second = doubleList2;
        this.inverted = bl;
    }

    public int size() {
        return this.first.size() + this.second.size();
    }

    @Override
    public boolean forEachPair(PairList.Consumer arg) {
        if (this.inverted) {
            return this.iterateSections((i, j, k) -> arg.merge(j, i, k));
        }
        return this.iterateSections(arg);
    }

    private boolean iterateSections(PairList.Consumer arg) {
        int i = this.first.size() - 1;
        for (int j = 0; j < i; ++j) {
            if (arg.merge(j, -1, j)) continue;
            return false;
        }
        if (!arg.merge(i, -1, i)) {
            return false;
        }
        for (int k = 0; k < this.second.size(); ++k) {
            if (arg.merge(i, k, i + 1 + k)) continue;
            return false;
        }
        return true;
    }

    public double getDouble(int i) {
        if (i < this.first.size()) {
            return this.first.getDouble(i);
        }
        return this.second.getDouble(i - this.first.size());
    }

    @Override
    public DoubleList getPairs() {
        return this;
    }
}

