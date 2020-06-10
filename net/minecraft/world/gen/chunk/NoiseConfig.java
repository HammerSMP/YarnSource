/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.NumberCodecs;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;

public class NoiseConfig {
    public static final Codec<NoiseConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NumberCodecs.rangedInt(0, 256).fieldOf("height").forGetter(NoiseConfig::getHeight), (App)NoiseSamplingConfig.CODEC.fieldOf("sampling").forGetter(NoiseConfig::getSampling), (App)SlideConfig.CODEC.fieldOf("top_slide").forGetter(NoiseConfig::getTopSlide), (App)SlideConfig.CODEC.fieldOf("bottom_slide").forGetter(NoiseConfig::getBottomSlide), (App)NumberCodecs.rangedInt(1, 4).fieldOf("size_horizontal").forGetter(NoiseConfig::getSizeHorizontal), (App)NumberCodecs.rangedInt(1, 4).fieldOf("size_vertical").forGetter(NoiseConfig::getSizeVertical), (App)Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseConfig::getDensityFactor), (App)Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseConfig::getDensityOffset), (App)Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseConfig::hasSimplexSurfaceNoise), (App)Codec.BOOL.optionalFieldOf("random_density_offset", (Object)false, Lifecycle.experimental()).forGetter(NoiseConfig::hasRandomDensityOffset), (App)Codec.BOOL.optionalFieldOf("island_noise_override", (Object)false, Lifecycle.experimental()).forGetter(NoiseConfig::hasIslandNoiseOverride), (App)Codec.BOOL.optionalFieldOf("amplified", (Object)false, Lifecycle.experimental()).forGetter(NoiseConfig::isAmplified)).apply((Applicative)instance, NoiseConfig::new));
    private final int height;
    private final NoiseSamplingConfig sampling;
    private final SlideConfig topSlide;
    private final SlideConfig bottomSlide;
    private final int horizontalSize;
    private final int verticalSize;
    private final double densityFactor;
    private final double densityOffset;
    private final boolean simplexSurfaceNoise;
    private final boolean randomDensityOffset;
    private final boolean islandNoiseOverride;
    private final boolean amplified;

    public NoiseConfig(int i, NoiseSamplingConfig arg, SlideConfig arg2, SlideConfig arg3, int j, int k, double d, double e, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.height = i;
        this.sampling = arg;
        this.topSlide = arg2;
        this.bottomSlide = arg3;
        this.horizontalSize = j;
        this.verticalSize = k;
        this.densityFactor = d;
        this.densityOffset = e;
        this.simplexSurfaceNoise = bl;
        this.randomDensityOffset = bl2;
        this.islandNoiseOverride = bl3;
        this.amplified = bl4;
    }

    public int getHeight() {
        return this.height;
    }

    public NoiseSamplingConfig getSampling() {
        return this.sampling;
    }

    public SlideConfig getTopSlide() {
        return this.topSlide;
    }

    public SlideConfig getBottomSlide() {
        return this.bottomSlide;
    }

    public int getSizeHorizontal() {
        return this.horizontalSize;
    }

    public int getSizeVertical() {
        return this.verticalSize;
    }

    public double getDensityFactor() {
        return this.densityFactor;
    }

    public double getDensityOffset() {
        return this.densityOffset;
    }

    @Deprecated
    public boolean hasSimplexSurfaceNoise() {
        return this.simplexSurfaceNoise;
    }

    @Deprecated
    public boolean hasRandomDensityOffset() {
        return this.randomDensityOffset;
    }

    @Deprecated
    public boolean hasIslandNoiseOverride() {
        return this.islandNoiseOverride;
    }

    @Deprecated
    public boolean isAmplified() {
        return this.amplified;
    }
}
