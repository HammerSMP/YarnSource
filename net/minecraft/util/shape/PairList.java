/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface PairList {
    public DoubleList getPairs();

    public boolean forEachPair(Consumer var1);

    public static interface Consumer {
        public boolean merge(int var1, int var2, int var3);
    }
}

