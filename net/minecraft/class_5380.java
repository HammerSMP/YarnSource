/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.class_5382;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public final class class_5380<E>
implements Codec<SimpleRegistry<E>> {
    private final Codec<SimpleRegistry<E>> field_25504;
    private final RegistryKey<Registry<E>> field_25505;
    private final Codec<E> field_25506;

    public static <E> class_5380<E> method_29745(RegistryKey<Registry<E>> arg, Lifecycle lifecycle, Codec<E> codec) {
        return new class_5380<E>(arg, lifecycle, codec);
    }

    private class_5380(RegistryKey<Registry<E>> arg, Lifecycle lifecycle, Codec<E> codec) {
        this.field_25504 = SimpleRegistry.method_29724(arg, lifecycle, codec);
        this.field_25505 = arg;
        this.field_25506 = codec;
    }

    public <T> DataResult<T> encode(SimpleRegistry<E> arg, DynamicOps<T> dynamicOps, T object) {
        return this.field_25504.encode(arg, dynamicOps, object);
    }

    public <T> DataResult<Pair<SimpleRegistry<E>, T>> decode(DynamicOps<T> dynamicOps, T object) {
        DataResult dataResult = this.field_25504.decode(dynamicOps, object);
        if (dynamicOps instanceof class_5382) {
            return dataResult.flatMap(pair -> ((class_5382)dynamicOps).method_29755((SimpleRegistry)pair.getFirst(), this.field_25505, this.field_25506).map(arg -> Pair.of((Object)arg, (Object)pair.getSecond())));
        }
        return dataResult;
    }

    public String toString() {
        return "RegistryFileCodec[" + this.field_25505 + " " + this.field_25506 + "]";
    }

    public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
        return this.encode((SimpleRegistry)object, dynamicOps, object2);
    }
}

