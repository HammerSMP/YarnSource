/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class BiomeSource
implements BiomeAccess.Storage {
    private static final List<Biome> SPAWN_BIOMES = Lists.newArrayList((Object[])new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
    protected final Map<StructureFeature<?>, Boolean> structureFeatures = Maps.newHashMap();
    protected final Set<BlockState> topMaterials = Sets.newHashSet();
    protected final Set<Biome> biomes;

    protected BiomeSource(Set<Biome> set) {
        this.biomes = set;
    }

    public List<Biome> getSpawnBiomes() {
        return SPAWN_BIOMES;
    }

    public Set<Biome> getBiomesInArea(int i, int j, int k, int l) {
        int m = i - l >> 2;
        int n = j - l >> 2;
        int o = k - l >> 2;
        int p = i + l >> 2;
        int q = j + l >> 2;
        int r = k + l >> 2;
        int s = p - m + 1;
        int t = q - n + 1;
        int u = r - o + 1;
        HashSet set = Sets.newHashSet();
        for (int v = 0; v < u; ++v) {
            for (int w = 0; w < s; ++w) {
                for (int x = 0; x < t; ++x) {
                    int y = m + w;
                    int z = n + x;
                    int aa = o + v;
                    set.add(this.getBiomeForNoiseGen(y, z, aa));
                }
            }
        }
        return set;
    }

    public BlockPos locateBiome(int i, int j, int k, int l, List<Biome> list, Random random) {
        return this.method_24385(i, j, k, l, 1, list, random, false);
    }

    @Nullable
    public BlockPos method_24385(int i, int j, int k, int l, int m, List<Biome> list, Random random, boolean bl) {
        int s;
        int n = i >> 2;
        int o = k >> 2;
        int p = l >> 2;
        int q = j >> 2;
        BlockPos lv = null;
        int r = 0;
        for (int t = s = bl ? 0 : p; t <= p; t += m) {
            for (int u = -t; u <= t; u += m) {
                boolean bl2 = Math.abs(u) == t;
                for (int v = -t; v <= t; v += m) {
                    int x;
                    int w;
                    if (bl) {
                        boolean bl3;
                        boolean bl4 = bl3 = Math.abs(v) == t;
                        if (!bl3 && !bl2) continue;
                    }
                    if (!list.contains(this.getBiomeForNoiseGen(w = n + v, q, x = o + u))) continue;
                    if (lv == null || random.nextInt(r + 1) == 0) {
                        lv = new BlockPos(w << 2, j, x << 2);
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

    public float getNoiseRange(int i, int j) {
        return 0.0f;
    }

    public boolean hasStructureFeature(StructureFeature<?> arg2) {
        return this.structureFeatures.computeIfAbsent(arg2, arg -> this.biomes.stream().anyMatch(arg2 -> arg2.hasStructureFeature(arg)));
    }

    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (Biome lv : this.biomes) {
                this.topMaterials.add(lv.getSurfaceConfig().getTopMaterial());
            }
        }
        return this.topMaterials;
    }
}

