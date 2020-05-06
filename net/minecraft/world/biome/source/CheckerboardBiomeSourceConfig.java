/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceConfig;

public class CheckerboardBiomeSourceConfig
implements BiomeSourceConfig {
    private Biome[] biomes = new Biome[]{Biomes.PLAINS};
    private int size = 1;

    public CheckerboardBiomeSourceConfig(long l) {
    }

    public CheckerboardBiomeSourceConfig setBiomes(Biome[] args) {
        this.biomes = args;
        return this;
    }

    public CheckerboardBiomeSourceConfig setSize(int i) {
        this.size = i;
        return this;
    }

    public Biome[] getBiomes() {
        return this.biomes;
    }

    public int getSize() {
        return this.size;
    }
}

