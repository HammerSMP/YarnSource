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
import net.minecraft.class_5455;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class RegistryReadingOps<T>
extends ForwardingDynamicOps<T> {
    private final class_5455 tracker;

    public static <T> RegistryReadingOps<T> of(DynamicOps<T> delegate, class_5455 tracker) {
        return new RegistryReadingOps<T>(delegate, tracker);
    }

    private RegistryReadingOps(DynamicOps<T> delegate, class_5455 tracker) {
        super(delegate);
        this.tracker = tracker;
    }

    protected <E> DataResult<T> encodeOrId(E input, T prefix, RegistryKey<? extends Registry<E>> registryReference, MapCodec<E> mapCodec) {
        MutableRegistry lv;
        Optional<RegistryKey<E>> optional2;
        Optional optional = this.tracker.method_30527(registryReference);
        if (optional.isPresent() && (optional2 = (lv = optional.get()).getKey(input)).isPresent()) {
            RegistryKey<E> lv2 = optional2.get();
            if (lv.isLoaded(lv2)) {
                return SimpleRegistry.method_30516(registryReference, mapCodec).codec().encode((Object)Pair.of(lv2, input), this.delegate, prefix);
            }
            return Identifier.CODEC.encode((Object)lv2.getValue(), this.delegate, prefix);
        }
        return mapCodec.codec().encode(input, (DynamicOps)this, prefix);
    }
}

