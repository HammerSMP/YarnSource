/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ProbabilityConfig
implements CarverConfig,
FeatureConfig {
    public static final Codec<ProbabilityConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").forGetter(arg -> Float.valueOf(arg.probability))).apply((Applicative)instance, ProbabilityConfig::new));
    public final float probability;

    public ProbabilityConfig(float f) {
        this.probability = f;
    }
}

