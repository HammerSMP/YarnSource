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

    public DisjointPairList(DoubleList first, DoubleList second, boolean inverted) {
        this.first = first;
        this.second = second;
        this.inverted = inverted;
    }

    public int size() {
        return this.first.size() + this.second.size();
    }

    @Override
    public boolean forEachPair(PairList.Consumer predicate) {
        if (this.inverted) {
            return this.iterateSections((i, j, k) -> predicate.merge(j, i, k));
        }
        return this.iterateSections(predicate);
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

    public double getDouble(int position) {
        if (position < this.first.size()) {
            return this.first.getDouble(position);
        }
        return this.second.getDouble(position - this.first.size());
    }

    @Override
    public DoubleList getPairs() {
        return this;
    }
}

