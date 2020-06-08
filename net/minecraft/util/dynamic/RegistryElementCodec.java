/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryElementCodec<E>
implements Codec<Supplier<E>> {
    private final RegistryKey<Registry<E>> registryRef;
    private final Codec<E> elementCodec;

    public static <E> RegistryElementCodec<E> of(RegistryKey<Registry<E>> arg, Codec<E> codec) {
        return new RegistryElementCodec<E>(arg, codec);
    }

    private RegistryElementCodec(RegistryKey<Registry<E>> arg, Codec<E> codec) {
        this.registryRef = arg;
        this.elementCodec = codec;
    }

    public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
        if (dynamicOps instanceof RegistryReadingOps) {
            return ((RegistryReadingOps)dynamicOps).encodeOrId(supplier.get(), object, this.registryRef, this.elementCodec);
        }
        return this.elementCodec.encode(supplier.get(), dynamicOps, object);
    }

    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> dynamicOps, T object) {
        if (dynamicOps instanceof RegistryOps) {
            return ((RegistryOps)dynamicOps).decodeOrId(object, this.registryRef, this.elementCodec);
        }
        return this.elementCodec.decode(dynamicOps, object).map(pair -> pair.mapFirst(object -> () -> object));
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
    }

    public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
        return this.encode((Supplier)object, dynamicOps, object2);
    }
}

