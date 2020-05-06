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

public class OffsetDoubleList
extends AbstractDoubleList {
    private final DoubleList oldList;
    private final double offset;

    public OffsetDoubleList(DoubleList doubleList, double d) {
        this.oldList = doubleList;
        this.offset = d;
    }

    public double getDouble(int i) {
        return this.oldList.getDouble(i) + this.offset;
    }

    public int size() {
        return this.oldList.size();
    }
}

