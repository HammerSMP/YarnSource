/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.state.property;

import java.util.Collection;
import java.util.Optional;

public interface Property<T extends Comparable<T>> {
    public String getName();

    public Collection<T> getValues();

    public Class<T> getType();

    public Optional<T> parse(String var1);

    public String name(T var1);
}

