/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class DefaultedRegistry<T>
extends SimpleRegistry<T> {
    private final Identifier defaultId;
    private T defaultValue;

    public DefaultedRegistry(String defaultId, RegistryKey<? extends Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
        this.defaultId = new Identifier(defaultId);
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> key, V entry) {
        if (this.defaultId.equals(key.getValue())) {
            this.defaultValue = entry;
        }
        return super.set(rawId, key, entry);
    }

    @Override
    public int getRawId(@Nullable T object) {
        int i = super.getRawId(object);
        return i == -1 ? super.getRawId(this.defaultValue) : i;
    }

    @Override
    @Nonnull
    public Identifier getId(T entry) {
        Identifier lv = super.getId(entry);
        return lv == null ? this.defaultId : lv;
    }

    @Override
    @Nonnull
    public T get(@Nullable Identifier id) {
        Object object = super.get(id);
        return object == null ? this.defaultValue : object;
    }

    @Override
    public Optional<T> getOrEmpty(@Nullable Identifier id) {
        return Optional.ofNullable(super.get(id));
    }

    @Override
    @Nonnull
    public T get(int index) {
        Object object = super.get(index);
        return object == null ? this.defaultValue : object;
    }

    @Override
    @Nonnull
    public T getRandom(Random random) {
        Object object = super.getRandom(random);
        return object == null ? this.defaultValue : object;
    }

    public Identifier getDefaultId() {
        return this.defaultId;
    }
}

