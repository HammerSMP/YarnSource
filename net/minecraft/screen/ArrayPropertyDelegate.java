/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import net.minecraft.screen.PropertyDelegate;

public class ArrayPropertyDelegate
implements PropertyDelegate {
    private final int[] data;

    public ArrayPropertyDelegate(int i) {
        this.data = new int[i];
    }

    @Override
    public int get(int i) {
        return this.data[i];
    }

    @Override
    public void set(int i, int j) {
        this.data[i] = j;
    }

    @Override
    public int size() {
        return this.data.length;
    }
}

