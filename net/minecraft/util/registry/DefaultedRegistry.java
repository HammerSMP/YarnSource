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
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.class_5321;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

public class DefaultedRegistry<T>
extends SimpleRegistry<T> {
    private final Identifier defaultId;
    private T defaultValue;

    public DefaultedRegistry(String string, class_5321<Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
        this.defaultId = new Identifier(string);
    }

    @Override
    public <V extends T> V set(int i, class_5321<T> arg, V object) {
        if (this.defaultId.equals(arg.method_29177())) {
            this.defaultValue = object;
        }
        return super.set(i, arg, object);
    }

    @Override
    public int getRawId(@Nullable T object) {
        int i = super.getRawId(object);
        return i == -1 ? super.getRawId(this.defaultValue) : i;
    }

    @Override
    @Nonnull
    public Identifier getId(T object) {
        Identifier lv = super.getId(object);
        return lv == null ? this.defaultId : lv;
    }

    @Override
    @Nonnull
    public T get(@Nullable Identifier arg) {
        Object object = super.get(arg);
        return object == null ? this.defaultValue : object;
    }

    @Override
    @Nonnull
    public T get(int i) {
        Object object = super.get(i);
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

