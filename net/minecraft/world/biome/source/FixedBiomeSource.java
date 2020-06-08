/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class FixedBiomeSource
extends BiomeSource {
    public static final Codec<FixedBiomeSource> field_24717 = Registry.BIOME.fieldOf("biome").xmap(FixedBiomeSource::new, arg -> arg.biome).stable().codec();
    private final Biome biome;

    public FixedBiomeSource(Biome arg) {
        super((List<Biome>)ImmutableList.of((Object)arg));
        this.biome = arg;
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return field_24717;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long l) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        return this.biome;
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int i, int j, int k, int l, int m, List<Biome> list, Random random, boolean bl) {
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

