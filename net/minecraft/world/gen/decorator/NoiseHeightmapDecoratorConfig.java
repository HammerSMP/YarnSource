/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class NoiseHeightmapDecoratorConfig
implements DecoratorConfig {
    public static final Codec<NoiseHeightmapDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("noise_level").forGetter(arg -> arg.noiseLevel), (App)Codec.INT.fieldOf("below_noise").forGetter(arg -> arg.belowNoise), (App)Codec.INT.fieldOf("above_noise").forGetter(arg -> arg.aboveNoise)).apply((Applicative)instance, NoiseHeightmapDecoratorConfig::new));
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public NoiseHeightmapDecoratorConfig(double d, int i, int j) {
        this.noiseLevel = d;
        this.belowNoise = i;
        this.aboveNoise = j;
    }
}

