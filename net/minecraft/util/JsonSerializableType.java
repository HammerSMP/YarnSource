/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

import net.minecraft.util.JsonSerializable;

public class JsonSerializableType<T> {
    private final JsonSerializable<? extends T> jsonSerializer;

    public JsonSerializableType(JsonSerializable<? extends T> arg) {
        this.jsonSerializer = arg;
    }

    public JsonSerializable<? extends T> getJsonSerializer() {
        return this.jsonSerializer;
    }
}

