/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public final class RegistryCodec<E>
implements Codec<SimpleRegistry<E>> {
    private final Codec<SimpleRegistry<E>> delegate;
    private final RegistryKey<? extends Registry<E>> registryRef;
    private final MapCodec<E> elementCodec;

    public static <E> RegistryCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle, MapCodec<E> mapCodec) {
        return new RegistryCodec<E>(registryRef, lifecycle, mapCodec);
    }

    private RegistryCodec(RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle, MapCodec<E> mapCodec) {
        this.delegate = SimpleRegistry.createEmptyCodec(registryRef, lifecycle, mapCodec);
        this.registryRef = registryRef;
        this.elementCodec = mapCodec;
    }

    public <T> DataResult<T> encode(SimpleRegistry<E> arg, DynamicOps<T> dynamicOps, T object) {
        return this.delegate.encode(arg, dynamicOps, object);
    }

    public <T> DataResult<Pair<SimpleRegistry<E>, T>> decode(DynamicOps<T> ops, T input) {
        DataResult dataResult = this.delegate.decode(ops, input);
        if (ops instanceof RegistryOps) {
            return dataResult.flatMap(pair -> ((RegistryOps)ops).loadToRegistry((SimpleRegistry)pair.getFirst(), this.registryRef, this.elementCodec).map(arg -> Pair.of((Object)arg, (Object)pair.getSecond())));
        }
        return dataResult;
    }

    public String toString() {
        return "RegistryDataPackCodec[" + this.delegate + " " + this.registryRef + " " + this.elementCodec + "]";
    }

    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((SimpleRegistry)input, ops, prefix);
    }
}

