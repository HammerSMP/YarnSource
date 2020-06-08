/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;

public class RegistryReadingOps<T>
extends ForwardingDynamicOps<T> {
    private final RegistryTracker tracker;

    public static <T> RegistryReadingOps<T> of(DynamicOps<T> dynamicOps, RegistryTracker arg) {
        return new RegistryReadingOps<T>(dynamicOps, arg);
    }

    private RegistryReadingOps(DynamicOps<T> dynamicOps, RegistryTracker arg) {
        super(dynamicOps);
        this.tracker = arg;
    }

    protected <E> DataResult<T> encodeOrId(E object, T object2, RegistryKey<Registry<E>> arg, Codec<E> codec) {
        Optional<RegistryKey<E>> optional2;
        Optional<MutableRegistry<E>> optional = this.tracker.get(arg);
        if (optional.isPresent() && (optional2 = optional.get().getKey(object)).isPresent()) {
            return Identifier.CODEC.encode((Object)optional2.get().getValue(), this.delegate, object2);
        }
        return codec.encode(object, this.delegate, object2);
    }
}

