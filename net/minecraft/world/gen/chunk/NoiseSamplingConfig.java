/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSamplingConfig {
    private static final Codec<Double> field_25188 = Codec.doubleRange((double)0.001, (double)1000.0);
    public static final Codec<NoiseSamplingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)field_25188.fieldOf("xz_scale").forGetter(NoiseSamplingConfig::getXZScale), (App)field_25188.fieldOf("y_scale").forGetter(NoiseSamplingConfig::getYScale), (App)field_25188.fieldOf("xz_factor").forGetter(NoiseSamplingConfig::getXZFactor), (App)field_25188.fieldOf("y_factor").forGetter(NoiseSamplingConfig::getYFactor)).apply((Applicative)instance, NoiseSamplingConfig::new));
    private final double xzScale;
    private final double yScale;
    private final double xzFactor;
    private final double yFactor;

    public NoiseSamplingConfig(double xzScale, double yScale, double xzFactor, double yFactor) {
        this.xzScale = xzScale;
        this.yScale = yScale;
        this.xzFactor = xzFactor;
        this.yFactor = yFactor;
    }

    public double getXZScale() {
        return this.xzScale;
    }

    public double getYScale() {
        return this.yScale;
    }

    public double getXZFactor() {
        return this.xzFactor;
    }

    public double getYFactor() {
        return this.yFactor;
    }
}

