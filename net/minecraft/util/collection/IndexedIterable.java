/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.util.collection;

import javax.annotation.Nullable;

public interface IndexedIterable<T>
extends Iterable<T> {
    public int getRawId(T var1);

    @Nullable
    public T get(int var1);
}

