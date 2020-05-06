/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;

public interface ResourceMetadataReader<T> {
    public String getKey();

    public T fromJson(JsonObject var1);
}

