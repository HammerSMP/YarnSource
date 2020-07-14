/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

import java.util.function.Supplier;

public class Lazy<T> {
    private Supplier<T> supplier;
    private T value;

    public Lazy(Supplier<T> delegate) {
        this.supplier = delegate;
    }

    public T get() {
        Supplier<T> supplier = this.supplier;
        if (supplier != null) {
            this.value = supplier.get();
            this.supplier = null;
        }
        return this.value;
    }
}

