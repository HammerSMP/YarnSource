/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class BiomeSource
implements BiomeAccess.Storage {
    public static final Codec<BiomeSource> field_24713;
    private static final List<Biome> SPAWN_BIOMES;
    protected final Map<StructureFeature<?>, Boolean> structureFeatures = Maps.newHashMap();
    protected final Set<BlockState> topMaterials = Sets.newHashSet();
    protected final List<Biome> biomes;

    protected BiomeSource(List<Biome> biomes) {
        this.biomes = biomes;
    }

    protected abstract Codec<? extends BiomeSource> method_28442();

    @Environment(value=EnvType.CLIENT)
    public abstract BiomeSource withSeed(long var1);

    public List<Biome> getSpawnBiomes() {
        return SPAWN_BIOMES;
    }

    public List<Biome> getBiomes() {
        return this.biomes;
    }

    public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
        int m = x - radius >> 2;
        int n = y - radius >> 2;
        int o = z - radius >> 2;
        int p = x + radius >> 2;
        int q = y + radius >> 2;
        int r = z + radius >> 2;
        int s = p - m + 1;
        int t = q - n + 1;
        int u = r - o + 1;
        HashSet set = Sets.newHashSet();
        for (int v = 0; v < u; ++v) {
            for (int w = 0; w < s; ++w) {
                for (int x2 = 0; x2 < t; ++x2) {
                    int y2 = m + w;
                    int z2 = n + x2;
                    int aa = o + v;
                    set.add(this.getBiomeForNoiseGen(y2, z2, aa));
                }
            }
        }
        return set;
    }

    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, List<Biome> biomes, Random random) {
        return this.locateBiome(x, y, z, radius, 1, biomes, random, false);
    }

    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, int m, List<Biome> biomes, Random random, boolean bl) {
        int s;
        int n = x >> 2;
        int o = z >> 2;
        int p = radius >> 2;
        int q = y >> 2;
        BlockPos lv = null;
        int r = 0;
        for (int t = s = bl ? 0 : p; t <= p; t += m) {
            for (int u = -t; u <= t; u += m) {
                boolean bl2 = Math.abs(u) == t;
                for (int v = -t; v <= t; v += m) {
                    int x2;
                    int w;
                    if (bl) {
                        boolean bl3;
                        boolean bl4 = bl3 = Math.abs(v) == t;
                        if (!bl3 && !bl2) continue;
                    }
                    if (!biomes.contains(this.getBiomeForNoiseGen(w = n + v, q, x2 = o + u))) continue;
                    if (lv == null || random.nextInt(r + 1) == 0) {
                        lv = new BlockPos(w << 2, y, x2 << 2);
                        if (bl) {
                            return lv;
                        }
                    }
                    ++r;
                }
            }
        }
        return lv;
    }

    public boolean hasStructureFeature(StructureFeature<?> feature) {
        return this.structureFeatures.computeIfAbsent(feature, arg -> this.biomes.stream().anyMatch(arg2 -> arg2.hasStructureFeature((StructureFeature<?>)arg)));
    }

    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (Biome lv : this.biomes) {
                this.topMaterials.add(lv.getSurfaceConfig().getTopMaterial());
            }
        }
        return this.topMaterials;
    }

    static {
        Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.field_24717);
        Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeSource.field_24715);
        Registry.register(Registry.BIOME_SOURCE, "vanilla_layered", VanillaLayeredBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.field_24730);
        field_24713 = Registry.BIOME_SOURCE.dispatchStable(BiomeSource::method_28442, Function.identity());
        SPAWN_BIOMES = Lists.newArrayList((Object[])new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
    }
}

