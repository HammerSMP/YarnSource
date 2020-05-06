/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class FractionalDoubleList
extends AbstractDoubleList {
    private final int sectionCount;

    FractionalDoubleList(int i) {
        this.sectionCount = i;
    }

    public double getDouble(int i) {
        return (double)i / (double)this.sectionCount;
    }

    public int size() {
        return this.sectionCount + 1;
    }
}

