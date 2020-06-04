/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public class class_5324 {
    private static Function<Integer, DataResult<Integer>> method_29232(int i, int j) {
        return integer -> {
            if (integer >= i && integer <= j) {
                return DataResult.success((Object)integer);
            }
            return DataResult.error((String)("Value " + integer + " outside of range [" + i + ":" + j + "]"), (Object)integer);
        };
    }

    public static Codec<Integer> method_29229(int i, int j) {
        Function<Integer, DataResult<Integer>> function = class_5324.method_29232(i, j);
        return Codec.INT.flatXmap(function, function);
    }

    private static Function<Double, DataResult<Double>> method_29231(double d, double e) {
        return double_ -> {
            if (double_ >= d && double_ <= e) {
                return DataResult.success((Object)double_);
            }
            return DataResult.error((String)("Value " + double_ + " outside of range [" + d + ":" + e + "]"), (Object)double_);
        };
    }

    public static Codec<Double> method_29227(double d, double e) {
        Function<Double, DataResult<Double>> function = class_5324.method_29231(d, e);
        return Codec.DOUBLE.flatXmap(function, function);
    }
}

