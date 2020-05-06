/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class VanillaLayeredBiomeSourceConfig
implements BiomeSourceConfig {
    private final long seed;
    private LevelGeneratorType generatorType = LevelGeneratorType.DEFAULT;
    private OverworldChunkGeneratorConfig generatorConfig = new OverworldChunkGeneratorConfig();

    public VanillaLayeredBiomeSourceConfig(long l) {
        this.seed = l;
    }

    public VanillaLayeredBiomeSourceConfig setGeneratorType(LevelGeneratorType arg) {
        this.generatorType = arg;
        return this;
    }

    public VanillaLayeredBiomeSourceConfig setGeneratorConfig(OverworldChunkGeneratorConfig arg) {
        this.generatorConfig = arg;
        return this;
    }

    public long getSeed() {
        return this.seed;
    }

    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    public OverworldChunkGeneratorConfig getGeneratorConfig() {
        return this.generatorConfig;
    }
}

