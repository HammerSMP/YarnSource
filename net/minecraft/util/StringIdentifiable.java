/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface StringIdentifiable {
    public String asString();

    public static <E extends Enum<E>> Codec<E> method_28140(Supplier<E[]> supplier, Function<? super String, ? extends E> function) {
        Enum[] enums = (Enum[])supplier.get();
        return StringIdentifiable.method_28141(Enum::ordinal, i -> enums[i], function);
    }

    public static <E extends StringIdentifiable> Codec<E> method_28141(final ToIntFunction<E> toIntFunction, final IntFunction<E> intFunction, final Function<? super String, ? extends E> function) {
        return new Codec<E>(){

            public <T> DataResult<T> encode(E arg, DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.mergeToPrimitive(object, dynamicOps.createInt(toIntFunction.applyAsInt(arg)));
                }
                return dynamicOps.mergeToPrimitive(object, dynamicOps.createString(arg.asString()));
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.getNumberValue(object).flatMap(number -> Optional.ofNullable(intFunction.apply(number.intValue())).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element id: " + number)))).map(arg -> Pair.of((Object)arg, (Object)dynamicOps.empty()));
                }
                return dynamicOps.getStringValue(object).flatMap(string -> Optional.ofNullable(function.apply(string)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element name: " + string)))).map(arg -> Pair.of((Object)arg, (Object)dynamicOps.empty()));
            }

            public String toString() {
                return "StringRepresentable[" + toIntFunction + "]";
            }

            public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
                return this.encode((E)((StringIdentifiable)object), (DynamicOps<T>)dynamicOps, (T)object2);
            }
        };
    }

    public static Keyable method_28142(final StringIdentifiable[] args) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                if (dynamicOps.compressMaps()) {
                    return IntStream.range(0, args.length).mapToObj(dynamicOps::createInt);
                }
                return Arrays.stream(args).map(StringIdentifiable::asString).map(dynamicOps::createString);
            }
        };
    }
}

