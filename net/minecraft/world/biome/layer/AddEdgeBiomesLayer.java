/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddEdgeBiomesLayer implements CrossSamplingLayer
{
    INSTANCE;

    private static final int BEACH_ID;
    private static final int SNOWY_BEACH_ID;
    private static final int DESERT_ID;
    private static final int MOUNTAINS_ID;
    private static final int WOODED_MOUNTAINS_ID;
    private static final int FOREST_ID;
    private static final int JUNGLE_ID;
    private static final int JUNGLE_EDGE_ID;
    private static final int JUNGLE_HILLS_ID;
    private static final int BADLANDS_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int BADLANDS_PLATEAU_ID;
    private static final int ERODED_BADLANDS_ID;
    private static final int MODIFIED_WOODED_BADLANDS_PLATEAU_ID;
    private static final int MODIFIED_BADLANDS_PLATEAU_ID;
    private static final int MUSHROOM_FIELDS_ID;
    private static final int MUSHROOM_FIELD_SHORE_ID;
    private static final int RIVER_ID;
    private static final int MOUNTAIN_EDGE_ID;
    private static final int STONE_SHORE_ID;
    private static final int SWAMP_ID;
    private static final int TAIGA_ID;

    @Override
    public int sample(LayerRandomnessSource arg, int i, int j, int k, int l, int m) {
        Biome lv = (Biome)BuiltinRegistries.BIOME.get(m);
        if (m == MUSHROOM_FIELDS_ID) {
            if (BiomeLayers.isShallowOcean(i) || BiomeLayers.isShallowOcean(j) || BiomeLayers.isShallowOcean(k) || BiomeLayers.isShallowOcean(l)) {
                return MUSHROOM_FIELD_SHORE_ID;
            }
        } else if (lv != null && lv.getCategory() == Biome.Category.JUNGLE) {
            if (!(AddEdgeBiomesLayer.isWooded(i) && AddEdgeBiomesLayer.isWooded(j) && AddEdgeBiomesLayer.isWooded(k) && AddEdgeBiomesLayer.isWooded(l))) {
                return JUNGLE_EDGE_ID;
            }
            if (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l)) {
                return BEACH_ID;
            }
        } else if (m == MOUNTAINS_ID || m == WOODED_MOUNTAINS_ID || m == MOUNTAIN_EDGE_ID) {
            if (!BiomeLayers.isOcean(m) && (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l))) {
                return STONE_SHORE_ID;
            }
        } else if (lv != null && lv.getPrecipitation() == Biome.Precipitation.SNOW) {
            if (!BiomeLayers.isOcean(m) && (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l))) {
                return SNOWY_BEACH_ID;
            }
        } else if (m == BADLANDS_ID || m == WOODED_BADLANDS_PLATEAU_ID) {
            if (!(BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l) || this.isBadlands(i) && this.isBadlands(j) && this.isBadlands(k) && this.isBadlands(l))) {
                return DESERT_ID;
            }
        } else if (!BiomeLayers.isOcean(m) && m != RIVER_ID && m != SWAMP_ID && (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l))) {
            return BEACH_ID;
        }
        return m;
    }

    private static boolean isWooded(int i) {
        if (BuiltinRegistries.BIOME.get(i) != null && ((Biome)BuiltinRegistries.BIOME.get(i)).getCategory() == Biome.Category.JUNGLE) {
            return true;
        }
        return i == JUNGLE_EDGE_ID || i == JUNGLE_ID || i == JUNGLE_HILLS_ID || i == FOREST_ID || i == TAIGA_ID || BiomeLayers.isOcean(i);
    }

    private boolean isBadlands(int i) {
        return i == BADLANDS_ID || i == WOODED_BADLANDS_PLATEAU_ID || i == BADLANDS_PLATEAU_ID || i == ERODED_BADLANDS_ID || i == MODIFIED_WOODED_BADLANDS_PLATEAU_ID || i == MODIFIED_BADLANDS_PLATEAU_ID;
    }

    static {
        BEACH_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BEACH);
        SNOWY_BEACH_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_BEACH);
        DESERT_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DESERT);
        MOUNTAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MOUNTAINS);
        WOODED_MOUNTAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.WOODED_MOUNTAINS);
        FOREST_ID = BuiltinRegistries.BIOME.getRawId(Biomes.FOREST);
        JUNGLE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.JUNGLE);
        JUNGLE_EDGE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.JUNGLE_EDGE);
        JUNGLE_HILLS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.JUNGLE_HILLS);
        BADLANDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BADLANDS);
        WOODED_BADLANDS_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
        BADLANDS_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
        ERODED_BADLANDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.ERODED_BADLANDS);
        MODIFIED_WOODED_BADLANDS_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
        MODIFIED_BADLANDS_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MODIFIED_BADLANDS_PLATEAU);
        MUSHROOM_FIELDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
        MUSHROOM_FIELD_SHORE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELD_SHORE);
        RIVER_ID = BuiltinRegistries.BIOME.getRawId(Biomes.RIVER);
        MOUNTAIN_EDGE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MOUNTAIN_EDGE);
        STONE_SHORE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.STONE_SHORE);
        SWAMP_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SWAMP);
        TAIGA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.TAIGA);
    }
}

