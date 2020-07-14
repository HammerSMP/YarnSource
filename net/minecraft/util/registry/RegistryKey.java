/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryKey<T> {
    private static final Map<String, RegistryKey<?>> INSTANCES = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final Identifier registry;
    private final Identifier value;

    public static <T> RegistryKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier value) {
        return RegistryKey.of(registry.value, value);
    }

    public static <T> RegistryKey<Registry<T>> ofRegistry(Identifier registry) {
        return RegistryKey.of(Registry.ROOT_KEY, registry);
    }

    private static <T> RegistryKey<T> of(Identifier registry, Identifier value) {
        String string2 = (registry + ":" + value).intern();
        return INSTANCES.computeIfAbsent(string2, string -> new RegistryKey(registry, value));
    }

    private RegistryKey(Identifier registry, Identifier value) {
        this.registry = registry;
        this.value = value;
    }

    public String toString() {
        return "ResourceKey[" + this.registry + " / " + this.value + ']';
    }

    public Identifier getValue() {
        return this.value;
    }

    public static <T> Function<Identifier, RegistryKey<T>> createKeyFactory(RegistryKey<? extends Registry<T>> registry) {
        return arg2 -> RegistryKey.of(registry, arg2);
    }
}

