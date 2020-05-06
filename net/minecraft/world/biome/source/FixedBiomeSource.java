/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;

public class FixedBiomeSource
extends BiomeSource {
    private final Biome biome;

    public FixedBiomeSource(FixedBiomeSourceConfig arg) {
        super((Set<Biome>)ImmutableSet.of((Object)arg.getBiome()));
        this.biome = arg.getBiome();
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        return this.biome;
    }

    @Override
    @Nullable
    public BlockPos method_24385(int i, int j, int k, int l, int m, List<Biome> list, Random random, boolean bl) {
        if (list.contains(this.biome)) {
            if (bl) {
                return new BlockPos(i, j, k);
            }
            return new BlockPos(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1));
        }
        return null;
    }

    @Override
    public Set<Biome> getBiomesInArea(int i, int j, int k, int l) {
        return Sets.newHashSet((Object[])new Biome[]{this.biome});
    }
}

