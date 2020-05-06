/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;

public enum HorizontalVoronoiBiomeAccessType implements BiomeAccessType
{
    INSTANCE;


    @Override
    public Biome getBiome(long l, int i, int j, int k, BiomeAccess.Storage arg) {
        return VoronoiBiomeAccessType.INSTANCE.getBiome(l, i, 0, k, arg);
    }
}

