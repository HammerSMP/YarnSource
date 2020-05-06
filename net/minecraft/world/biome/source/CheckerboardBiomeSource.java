/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;

public class CheckerboardBiomeSource
extends BiomeSource {
    private final Biome[] biomeArray;
    private final int gridSize;

    public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig arg) {
        super((Set<Biome>)ImmutableSet.copyOf((Object[])arg.getBiomes()));
        this.biomeArray = arg.getBiomes();
        this.gridSize = arg.getSize() + 2;
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        return this.biomeArray[Math.floorMod((i >> this.gridSize) + (k >> this.gridSize), this.biomeArray.length)];
    }
}

