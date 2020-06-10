/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class NumberCodecs {
    private static Function<Integer, DataResult<Integer>> createRangedDataResultFactory(int i, int j) {
        return integer -> {
            if (integer >= i && integer <= j) {
                return DataResult.success((Object)integer);
            }
            return DataResult.error((String)("Value " + integer + " outside of range [" + i + ":" + j + "]"), (Object)integer);
        };
    }

    public static Codec<Integer> rangedInt(int i, int j) {
        Function<Integer, DataResult<Integer>> function = NumberCodecs.createRangedDataResultFactory(i, j);
        return Codec.INT.flatXmap(function, function);
    }

    private static Function<Double, DataResult<Double>> createRangedDataResultFactory(double d, double e) {
        return double_ -> {
            if (double_ >= d && double_ <= e) {
                return DataResult.success((Object)double_);
            }
            return DataResult.error((String)("Value " + double_ + " outside of range [" + d + ":" + e + "]"), (Object)double_);
        };
    }

    public static Codec<Double> rangedDouble(double d, double e) {
        Function<Double, DataResult<Double>> function = NumberCodecs.createRangedDataResultFactory(d, e);
        return Codec.DOUBLE.flatXmap(function, function);
    }

    public static <T> MapCodec<Pair<RegistryKey<T>, T>> method_29906(RegistryKey<Registry<T>> arg, MapCodec<T> mapCodec) {
        return Codec.mapPair((MapCodec)Identifier.CODEC.xmap(RegistryKey.createKeyFactory(arg), RegistryKey::getValue).fieldOf("name"), mapCodec);
    }

    private static <A> MapCodec<A> method_29904(final MapCodec<A> mapCodec, final class_5395<A> arg) {
        return new MapCodec<A>(){

            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                return mapCodec.keys(dynamicOps);
            }

            public <T> RecordBuilder<T> encode(A object, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
                return arg.method_29908(dynamicOps, object, mapCodec.encode(object, dynamicOps, recordBuilder));
            }

            public <T> DataResult<A> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
                return arg.method_29907(dynamicOps, mapLike, mapCodec.decode(dynamicOps, mapLike));
            }

            public String toString() {
                return (Object)mapCodec + "[mapResult " + arg + "]";
            }
        };
    }

    public static <A> MapCodec<A> method_29905(MapCodec<A> mapCodec, final Consumer<String> consumer, final Supplier<? extends A> supplier) {
        return NumberCodecs.method_29904(mapCodec, new class_5395<A>(){

            @Override
            public <T> DataResult<A> method_29907(DynamicOps<T> dynamicOps, MapLike<T> mapLike, DataResult<A> dataResult) {
                return DataResult.success(dataResult.resultOrPartial(consumer).orElseGet(supplier));
            }

            @Override
            public <T> RecordBuilder<T> method_29908(DynamicOps<T> dynamicOps, A object, RecordBuilder<T> recordBuilder) {
                return recordBuilder;
            }

            public String toString() {
                return "WithDefault[" + supplier.get() + "]";
            }
        });
    }

    static interface class_5395<A> {
        public <T> DataResult<A> method_29907(DynamicOps<T> var1, MapLike<T> var2, DataResult<A> var3);

        public <T> RecordBuilder<T> method_29908(DynamicOps<T> var1, A var2, RecordBuilder<T> var3);
    }
}

