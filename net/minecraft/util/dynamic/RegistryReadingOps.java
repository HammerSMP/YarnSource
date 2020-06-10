/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.dynamic.NumberCodecs;
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

    protected <E> DataResult<T> encodeOrId(E object, T object2, RegistryKey<Registry<E>> arg, MapCodec<E> mapCodec) {
        MutableRegistry<E> lv;
        Optional<RegistryKey<E>> optional2;
        Optional<MutableRegistry<E>> optional = this.tracker.get(arg);
        if (optional.isPresent() && (optional2 = (lv = optional.get()).getKey(object)).isPresent()) {
            RegistryKey<E> lv2 = optional2.get();
            if (lv.isLoaded(lv2)) {
                return NumberCodecs.method_29906(arg, mapCodec).codec().encode((Object)Pair.of(lv2, object), this.delegate, object2);
            }
            return Identifier.CODEC.encode((Object)lv2.getValue(), this.delegate, object2);
        }
        return mapCodec.codec().encode(object, this.delegate, object2);
    }
}

