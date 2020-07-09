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

    public static <T> RegistryReadingOps<T> of(DynamicOps<T> dynamicOps, class_5455 arg) {
        return new RegistryReadingOps<T>(dynamicOps, arg);
    }

    private RegistryReadingOps(DynamicOps<T> dynamicOps, class_5455 arg) {
        super(dynamicOps);
        this.tracker = arg;
    }

    protected <E> DataResult<T> encodeOrId(E object, T object2, RegistryKey<? extends Registry<E>> arg, MapCodec<E> mapCodec) {
        MutableRegistry lv;
        Optional<RegistryKey<E>> optional2;
        Optional optional = this.tracker.method_30527(arg);
        if (optional.isPresent() && (optional2 = (lv = optional.get()).getKey(object)).isPresent()) {
            RegistryKey<E> lv2 = optional2.get();
            if (lv.isLoaded(lv2)) {
                return SimpleRegistry.method_30516(arg, mapCodec).codec().encode((Object)Pair.of(lv2, object), this.delegate, object2);
            }
            return Identifier.CODEC.encode((Object)lv2.getValue(), this.delegate, object2);
        }
        return mapCodec.codec().encode(object, (DynamicOps)this, object2);
    }
}

