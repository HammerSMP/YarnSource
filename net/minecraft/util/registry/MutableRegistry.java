/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.class_5321;
import net.minecraft.util.registry.Registry;

public abstract class MutableRegistry<T>
extends Registry<T> {
    public MutableRegistry(class_5321<Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
    }

    public abstract <V extends T> V set(int var1, class_5321<T> var2, V var3);

    public abstract <V extends T> V add(class_5321<T> var1, V var2);

    public String toString() {
        return "Registry[" + field_25101.getId(this) + "]";
    }
}

