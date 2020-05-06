/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import javax.annotation.Nullable;

public interface Clearable {
    public void clear();

    public static void clear(@Nullable Object object) {
        if (object instanceof Clearable) {
            ((Clearable)object).clear();
        }
    }
}

