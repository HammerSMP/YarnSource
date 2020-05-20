/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class CheckerboardBiomeSource
extends BiomeSource {
    private final Biome[] biomeArray;
    private final int gridSize;

    public CheckerboardBiomeSource(Biome[] args, int i) {
        super((Set<Biome>)ImmutableSet.copyOf((Object[])args));
        this.biomeArray = args;
        this.gridSize = i + 2;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource create(long l) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        return this.biomeArray[Math.floorMod((i >> this.gridSize) + (k >> this.gridSize), this.biomeArray.length)];
    }
}

