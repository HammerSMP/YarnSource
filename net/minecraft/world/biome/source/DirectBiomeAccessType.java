/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeAccessType;

public enum DirectBiomeAccessType implements BiomeAccessType
{
    INSTANCE;


    @Override
    public Biome getBiome(long l, int i, int j, int k, BiomeAccess.Storage arg) {
        return arg.getBiomeForNoiseGen(i >> 2, j >> 2, k >> 2);
    }
}

