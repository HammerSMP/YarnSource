/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public abstract class ServerConfigEntry<T> {
    @Nullable
    private final T key;

    public ServerConfigEntry(@Nullable T key) {
        this.key = key;
    }

    @Nullable
    T getKey() {
        return this.key;
    }

    boolean isInvalid() {
        return false;
    }

    protected abstract void fromJson(JsonObject var1);
}

