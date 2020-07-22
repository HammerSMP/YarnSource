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
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class FixedBiomeSource
extends BiomeSource {
    public static final Codec<FixedBiomeSource> field_24717 = Biome.field_24677.fieldOf("biome").xmap(FixedBiomeSource::new, arg -> arg.biome).stable().codec();
    private final Supplier<Biome> biome;

    public FixedBiomeSource(Biome arg) {
        this(() -> arg);
    }

    public FixedBiomeSource(Supplier<Biome> supplier) {
        super((List<Biome>)ImmutableList.of((Object)supplier.get()));
        this.biome = supplier;
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return field_24717;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biome.get();
    }

    @Override
    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, int m, List<Biome> biomes, Random random, boolean bl) {
        if (biomes.contains(this.biome.get())) {
            if (bl) {
                return new BlockPos(x, y, z);
            }
            return new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1));
        }
        return null;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
        return Sets.newHashSet((Object[])new Biome[]{this.biome.get()});
    }
}

