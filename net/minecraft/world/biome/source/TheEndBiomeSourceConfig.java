/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.source.BiomeSourceConfig;

public class TheEndBiomeSourceConfig
implements BiomeSourceConfig {
    private final long seed;

    public TheEndBiomeSourceConfig(long l) {
        this.seed = l;
    }

    public long getSeed() {
        return this.seed;
    }
}

