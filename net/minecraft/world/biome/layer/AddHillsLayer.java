/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AddHillsLayer implements MergingLayer,
NorthWestCoordinateTransformer
{
    INSTANCE;

    private static final Logger LOGGER;
    private static final int BIRCH_FOREST_ID;
    private static final int BIRCH_FOREST_HILLS_ID;
    private static final int DESERT_ID;
    private static final int DESERT_HILLS_ID;
    private static final int MOUNTAINS_ID;
    private static final int WOODED_MOUNTAINS_ID;
    private static final int FOREST_ID;
    private static final int WOODED_HILLS_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int SNOWY_MOUNTAINS_ID;
    private static final int JUNGLE_ID;
    private static final int JUNGLE_HILLS_ID;
    private static final int BAMBOO_JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_HILLS_ID;
    private static final int BADLANDS_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int PLAINS_ID;
    private static final int GIANT_TREE_TAIGA_ID;
    private static final int GIANT_TREE_TAIGA_HILLS_ID;
    private static final int DARK_FOREST_ID;
    private static final int SAVANNA_ID;
    private static final int SAVANNA_PLATEAU_ID;
    private static final int TAIGA_ID;
    private static final int SNOWY_TAIGA_ID;
    private static final int SNOWY_TAIGA_HILLS_ID;
    private static final int TAIGA_HILLS_ID;

    @Override
    public int sample(LayerRandomnessSource arg, LayerSampler arg2, LayerSampler arg3, int i, int j) {
        Biome lv;
        int k = arg2.sample(this.transformX(i + 1), this.transformZ(j + 1));
        int l = arg3.sample(this.transformX(i + 1), this.transformZ(j + 1));
        if (k > 255) {
            LOGGER.debug("old! {}", (Object)k);
        }
        int m = (l - 2) % 29;
        if (!(BiomeLayers.isShallowOcean(k) || l < 2 || m != 1 || (lv = (Biome)BuiltinRegistries.BIOME.get(k)) != null && lv.hasParent())) {
            Biome lv2 = Biomes.method_30360(lv);
            return lv2 == null ? k : BuiltinRegistries.BIOME.getRawId(lv2);
        }
        if (arg.nextInt(3) == 0 || m == 0) {
            int n = k;
            if (k == DESERT_ID) {
                n = DESERT_HILLS_ID;
            } else if (k == FOREST_ID) {
                n = WOODED_HILLS_ID;
            } else if (k == BIRCH_FOREST_ID) {
                n = BIRCH_FOREST_HILLS_ID;
            } else if (k == DARK_FOREST_ID) {
                n = PLAINS_ID;
            } else if (k == TAIGA_ID) {
                n = TAIGA_HILLS_ID;
            } else if (k == GIANT_TREE_TAIGA_ID) {
                n = GIANT_TREE_TAIGA_HILLS_ID;
            } else if (k == SNOWY_TAIGA_ID) {
                n = SNOWY_TAIGA_HILLS_ID;
            } else if (k == PLAINS_ID) {
                n = arg.nextInt(3) == 0 ? WOODED_HILLS_ID : FOREST_ID;
            } else if (k == SNOWY_TUNDRA_ID) {
                n = SNOWY_MOUNTAINS_ID;
            } else if (k == JUNGLE_ID) {
                n = JUNGLE_HILLS_ID;
            } else if (k == BAMBOO_JUNGLE_ID) {
                n = BAMBOO_JUNGLE_HILLS_ID;
            } else if (k == BiomeLayers.OCEAN_ID) {
                n = BiomeLayers.DEEP_OCEAN_ID;
            } else if (k == BiomeLayers.LUKEWARM_OCEAN_ID) {
                n = BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
            } else if (k == BiomeLayers.COLD_OCEAN_ID) {
                n = BiomeLayers.DEEP_COLD_OCEAN_ID;
            } else if (k == BiomeLayers.FROZEN_OCEAN_ID) {
                n = BiomeLayers.DEEP_FROZEN_OCEAN_ID;
            } else if (k == MOUNTAINS_ID) {
                n = WOODED_MOUNTAINS_ID;
            } else if (k == SAVANNA_ID) {
                n = SAVANNA_PLATEAU_ID;
            } else if (BiomeLayers.areSimilar(k, WOODED_BADLANDS_PLATEAU_ID)) {
                n = BADLANDS_ID;
            } else if ((k == BiomeLayers.DEEP_OCEAN_ID || k == BiomeLayers.DEEP_LUKEWARM_OCEAN_ID || k == BiomeLayers.DEEP_COLD_OCEAN_ID || k == BiomeLayers.DEEP_FROZEN_OCEAN_ID) && arg.nextInt(3) == 0) {
                int n2 = n = arg.nextInt(2) == 0 ? PLAINS_ID : FOREST_ID;
            }
            if (m == 0 && n != k) {
                Biome lv3 = Biomes.method_30360((Biome)BuiltinRegistries.BIOME.get(n));
                int n3 = n = lv3 == null ? k : BuiltinRegistries.BIOME.getRawId(lv3);
            }
            if (n != k) {
                int o = 0;
                if (BiomeLayers.areSimilar(arg2.sample(this.transformX(i + 1), this.transformZ(j + 0)), k)) {
                    ++o;
                }
                if (BiomeLayers.areSimilar(arg2.sample(this.transformX(i + 2), this.transformZ(j + 1)), k)) {
                    ++o;
                }
                if (BiomeLayers.areSimilar(arg2.sample(this.transformX(i + 0), this.transformZ(j + 1)), k)) {
                    ++o;
                }
                if (BiomeLayers.areSimilar(arg2.sample(this.transformX(i + 1), this.transformZ(j + 2)), k)) {
                    ++o;
                }
                if (o >= 3) {
                    return n;
                }
            }
        }
        return k;
    }

    static {
        LOGGER = LogManager.getLogger();
        BIRCH_FOREST_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BIRCH_FOREST);
        BIRCH_FOREST_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BIRCH_FOREST_HILLS);
        DESERT_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DESERT);
        DESERT_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DESERT_HILLS);
        MOUNTAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MOUNTAINS);
        WOODED_MOUNTAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.WOODED_MOUNTAINS);
        FOREST_ID = BuiltinRegistries.BIOME.getRawId(Biomes.FOREST);
        WOODED_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.WOODED_HILLS);
        SNOWY_TUNDRA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
        SNOWY_MOUNTAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_MOUNTAINS);
        JUNGLE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.JUNGLE);
        JUNGLE_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.JUNGLE_HILLS);
        BAMBOO_JUNGLE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BAMBOO_JUNGLE);
        BAMBOO_JUNGLE_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BAMBOO_JUNGLE_HILLS);
        BADLANDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BADLANDS);
        WOODED_BADLANDS_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
        PLAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.PLAINS);
        GIANT_TREE_TAIGA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
        GIANT_TREE_TAIGA_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA_HILLS);
        DARK_FOREST_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DARK_FOREST);
        SAVANNA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SAVANNA);
        SAVANNA_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SAVANNA_PLATEAU);
        TAIGA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.TAIGA);
        SNOWY_TAIGA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_TAIGA);
        SNOWY_TAIGA_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_TAIGA_HILLS);
        TAIGA_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.TAIGA_HILLS);
    }
}

