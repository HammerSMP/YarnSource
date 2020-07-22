/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public abstract class MutableRegistry<T>
extends Registry<T> {
    public MutableRegistry(RegistryKey<? extends Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
    }

    public abstract <V extends T> V set(int var1, RegistryKey<T> var2, V var3);

    public abstract <V extends T> V add(RegistryKey<T> var1, V var2);

    public abstract void markLoaded(RegistryKey<T> var1);
}

