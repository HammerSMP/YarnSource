/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Supplier;
import net.minecraft.class_5382;
import net.minecraft.class_5384;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class class_5381<E>
implements Codec<Supplier<E>> {
    private final RegistryKey<Registry<E>> field_25507;
    private final Codec<E> field_25508;

    public static <E> class_5381<E> method_29749(RegistryKey<Registry<E>> arg, Codec<E> codec) {
        return new class_5381<E>(arg, codec);
    }

    private class_5381(RegistryKey<Registry<E>> arg, Codec<E> codec) {
        this.field_25507 = arg;
        this.field_25508 = codec;
    }

    public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
        if (dynamicOps instanceof class_5384) {
            return ((class_5384)dynamicOps).method_29772(supplier.get(), object, this.field_25507, this.field_25508);
        }
        return this.field_25508.encode(supplier.get(), dynamicOps, object);
    }

    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> dynamicOps, T object) {
        if (dynamicOps instanceof class_5382) {
            return ((class_5382)dynamicOps).method_29759(object, this.field_25507, this.field_25508);
        }
        return this.field_25508.decode(dynamicOps, object).map(pair -> pair.mapFirst(object -> () -> object));
    }

    public String toString() {
        return "RegistryFileCodec[" + this.field_25507 + " " + this.field_25508 + "]";
    }

    public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
        return this.encode((Supplier)object, dynamicOps, object2);
    }
}

