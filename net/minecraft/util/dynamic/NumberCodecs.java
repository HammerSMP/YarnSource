/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

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
}

