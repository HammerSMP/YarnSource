/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class UniformIntDistribution {
    public static final Codec<UniformIntDistribution> CODEC = Codec.either((Codec)Codec.INT, (Codec)RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("base").forGetter(arg -> arg.base), (App)Codec.INT.fieldOf("spread").forGetter(arg -> arg.spread)).apply((Applicative)instance, UniformIntDistribution::new)).comapFlatMap(arg -> {
        if (arg.spread < 0) {
            return DataResult.error((String)("Spread must be non-negative, got: " + arg.spread));
        }
        return DataResult.success((Object)arg);
    }, Function.identity())).xmap(either -> (UniformIntDistribution)either.map(UniformIntDistribution::of, arg -> arg), arg -> arg.spread == 0 ? Either.left((Object)arg.base) : Either.right((Object)arg));
    private final int base;
    private final int spread;

    public static Codec<UniformIntDistribution> createValidatedCodec(int minBase, int maxBase, int maxSpread) {
        Function<UniformIntDistribution, DataResult> function = arg -> {
            if (arg.base >= minBase && arg.base <= maxBase) {
                if (arg.spread <= maxSpread) {
                    return DataResult.success((Object)arg);
                }
                return DataResult.error((String)("Spread too big: " + arg.spread + " > " + maxSpread));
            }
            return DataResult.error((String)("Base value out of range: " + arg.base + " [" + minBase + "-" + maxBase + "]"));
        };
        return CODEC.flatXmap(function, function);
    }

    private UniformIntDistribution(int base, int spread) {
        this.base = base;
        this.spread = spread;
    }

    public static UniformIntDistribution of(int value) {
        return new UniformIntDistribution(value, 0);
    }

    public static UniformIntDistribution of(int base, int spread) {
        return new UniformIntDistribution(base, spread);
    }

    public int getValue(Random random) {
        if (this.spread == 0) {
            return this.base;
        }
        return this.base + random.nextInt(this.spread + 1);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        UniformIntDistribution lv = (UniformIntDistribution)object;
        return this.base == lv.base && this.spread == lv.spread;
    }

    public int hashCode() {
        return Objects.hash(this.base, this.spread);
    }

    public String toString() {
        return "[" + this.base + '-' + (this.base + this.spread) + ']';
    }
}

