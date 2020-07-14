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

    public OffsetDoubleList(DoubleList oldList, double offset) {
        this.oldList = oldList;
        this.offset = offset;
    }

    public double getDouble(int position) {
        return this.oldList.getDouble(position) + this.offset;
    }

    public int size() {
        return this.oldList.size();
    }
}

