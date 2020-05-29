/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface class_5335<T> {
    public void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

    public T fromJson(JsonObject var1, JsonDeserializationContext var2);
}

