/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceConfig;

public class FixedBiomeSourceConfig
implements BiomeSourceConfig {
    private Biome biome = Biomes.PLAINS;

    public FixedBiomeSourceConfig(long l) {
    }

    public FixedBiomeSourceConfig setBiome(Biome arg) {
        this.biome = arg;
        return this;
    }

    public Biome getBiome() {
        return this.biome;
    }
}

