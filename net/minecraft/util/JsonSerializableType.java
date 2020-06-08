/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

import net.minecraft.util.JsonSerializer;

public class JsonSerializableType<T> {
    private final JsonSerializer<? extends T> jsonSerializer;

    public JsonSerializableType(JsonSerializer<? extends T> arg) {
        this.jsonSerializer = arg;
    }

    public JsonSerializer<? extends T> getJsonSerializer() {
        return this.jsonSerializer;
    }
}

