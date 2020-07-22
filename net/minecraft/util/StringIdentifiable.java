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

    public static <E extends Enum<E>> Codec<E> createCodec(Supplier<E[]> enumValues, Function<? super String, ? extends E> fromString) {
        Enum[] enums = (Enum[])enumValues.get();
        return StringIdentifiable.createCodec(Enum::ordinal, ordinal -> enums[ordinal], fromString);
    }

    public static <E extends StringIdentifiable> Codec<E> createCodec(final ToIntFunction<E> compressedEncoder, final IntFunction<E> compressedDecoder, final Function<? super String, ? extends E> decoder) {
        return new Codec<E>(){

            public <T> DataResult<T> encode(E arg, DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.mergeToPrimitive(object, dynamicOps.createInt(compressedEncoder.applyAsInt(arg)));
                }
                return dynamicOps.mergeToPrimitive(object, dynamicOps.createString(arg.asString()));
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.getNumberValue(object).flatMap(number -> Optional.ofNullable(compressedDecoder.apply(number.intValue())).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element id: " + number)))).map(arg -> Pair.of((Object)arg, (Object)dynamicOps.empty()));
                }
                return dynamicOps.getStringValue(object).flatMap(string -> Optional.ofNullable(decoder.apply(string)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element name: " + string)))).map(arg -> Pair.of((Object)arg, (Object)dynamicOps.empty()));
            }

            public String toString() {
                return "StringRepresentable[" + compressedEncoder + "]";
            }

            public /* synthetic */ DataResult encode(Object value, DynamicOps dynamicOps, Object object2) {
                return this.encode((E)((StringIdentifiable)value), (DynamicOps<T>)dynamicOps, (T)object2);
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

