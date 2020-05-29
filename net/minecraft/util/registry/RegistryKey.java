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

    public static <T> RegistryKey<T> of(RegistryKey<Registry<T>> arg, Identifier arg2) {
        return RegistryKey.of(arg.value, arg2);
    }

    public static <T> RegistryKey<Registry<T>> ofRegistry(Identifier arg) {
        return RegistryKey.of(Registry.ROOT_KEY, arg);
    }

    private static <T> RegistryKey<T> of(Identifier arg, Identifier arg2) {
        String string2 = (arg + ":" + arg2).intern();
        return INSTANCES.computeIfAbsent(string2, string -> new RegistryKey(arg, arg2));
    }

    private RegistryKey(Identifier arg, Identifier arg2) {
        this.registry = arg;
        this.value = arg2;
    }

    public String toString() {
        return "ResourceKey[" + this.registry + " / " + this.value + ']';
    }

    public Identifier getValue() {
        return this.value;
    }

    public static <T> Function<Identifier, RegistryKey<T>> createKeyFactory(RegistryKey<Registry<T>> arg) {
        return arg2 -> RegistryKey.of(arg, arg2);
    }
}

