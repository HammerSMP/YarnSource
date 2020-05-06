/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.storage;

import com.mojang.datafixers.types.DynamicOps;

public interface StorageSerializer<O> {
    public <T> T serialize(O var1, DynamicOps<T> var2);
}

