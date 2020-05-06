/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import javax.annotation.Nullable;

public class ThrowableDeliverer<T extends Throwable> {
    @Nullable
    private T throwable;

    public void add(T throwable) {
        if (this.throwable == null) {
            this.throwable = throwable;
        } else {
            ((Throwable)this.throwable).addSuppressed((Throwable)throwable);
        }
    }

    public void deliver() throws Throwable {
        if (this.throwable != null) {
            throw this.throwable;
        }
    }
}

