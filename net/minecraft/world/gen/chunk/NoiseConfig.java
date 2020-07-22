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
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;

public class NoiseConfig {
    public static final Codec<NoiseConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)256).fieldOf("height").forGetter(NoiseConfig::getHeight), (App)NoiseSamplingConfig.CODEC.fieldOf("sampling").forGetter(NoiseConfig::getSampling), (App)SlideConfig.CODEC.fieldOf("top_slide").forGetter(NoiseConfig::getTopSlide), (App)SlideConfig.CODEC.fieldOf("bottom_slide").forGetter(NoiseConfig::getBottomSlide), (App)Codec.intRange((int)1, (int)4).fieldOf("size_horizontal").forGetter(NoiseConfig::getSizeHorizontal), (App)Codec.intRange((int)1, (int)4).fieldOf("size_vertical").forGetter(NoiseConfig::getSizeVertical), (App)Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseConfig::getDensityFactor), (App)Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseConfig::getDensityOffset), (App)Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseConfig::hasSimplexSurfaceNoise), (App)Codec.BOOL.optionalFieldOf("random_density_offset", (Object)false, Lifecycle.experimental()).forGetter(NoiseConfig::hasRandomDensityOffset), (App)Codec.BOOL.optionalFieldOf("island_noise_override", (Object)false, Lifecycle.experimental()).forGetter(NoiseConfig::hasIslandNoiseOverride), (App)Codec.BOOL.optionalFieldOf("amplified", (Object)false, Lifecycle.experimental()).forGetter(NoiseConfig::isAmplified)).apply((Applicative)instance, NoiseConfig::new));
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

    public NoiseConfig(int height, NoiseSamplingConfig sampling, SlideConfig topSlide, SlideConfig bottomSlide, int sizeHorizontal, int sizeVertical, double densityFactor, double densityOffset, boolean simplexSurfaceNoise, boolean randomDensityOffset, boolean islandNoiseOverride, boolean amplified) {
        this.height = height;
        this.sampling = sampling;
        this.topSlide = topSlide;
        this.bottomSlide = bottomSlide;
        this.horizontalSize = sizeHorizontal;
        this.verticalSize = sizeVertical;
        this.densityFactor = densityFactor;
        this.densityOffset = densityOffset;
        this.simplexSurfaceNoise = simplexSurfaceNoise;
        this.randomDensityOffset = randomDensityOffset;
        this.islandNoiseOverride = islandNoiseOverride;
        this.amplified = amplified;
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

