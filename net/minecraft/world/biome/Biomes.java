/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.biome;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class Biomes {
    public static final IdList<Biome> MUTATED_BIOMES = new IdList();
    public static final Biome OCEAN;
    public static final Biome DEFAULT;
    public static final Biome PLAINS;
    public static final Biome DESERT;
    public static final Biome MOUNTAINS;
    public static final Biome FOREST;
    public static final Biome TAIGA;
    public static final Biome SWAMP;
    public static final Biome RIVER;
    public static final Biome NETHER_WASTES;
    public static final Biome THE_END;
    public static final Biome FROZEN_OCEAN;
    public static final Biome FROZEN_RIVER;
    public static final Biome SNOWY_TUNDRA;
    public static final Biome SNOWY_MOUNTAINS;
    public static final Biome MUSHROOM_FIELDS;
    public static final Biome MUSHROOM_FIELD_SHORE;
    public static final Biome BEACH;
    public static final Biome DESERT_HILLS;
    public static final Biome WOODED_HILLS;
    public static final Biome TAIGA_HILLS;
    public static final Biome MOUNTAIN_EDGE;
    public static final Biome JUNGLE;
    public static final Biome JUNGLE_HILLS;
    public static final Biome JUNGLE_EDGE;
    public static final Biome DEEP_OCEAN;
    public static final Biome STONE_SHORE;
    public static final Biome SNOWY_BEACH;
    public static final Biome BIRCH_FOREST;
    public static final Biome BIRCH_FOREST_HILLS;
    public static final Biome DARK_FOREST;
    public static final Biome SNOWY_TAIGA;
    public static final Biome SNOWY_TAIGA_HILLS;
    public static final Biome GIANT_TREE_TAIGA;
    public static final Biome GIANT_TREE_TAIGA_HILLS;
    public static final Biome WOODED_MOUNTAINS;
    public static final Biome SAVANNA;
    public static final Biome SAVANNA_PLATEAU;
    public static final Biome BADLANDS;
    public static final Biome WOODED_BADLANDS_PLATEAU;
    public static final Biome BADLANDS_PLATEAU;
    public static final Biome SMALL_END_ISLANDS;
    public static final Biome END_MIDLANDS;
    public static final Biome END_HIGHLANDS;
    public static final Biome END_BARRENS;
    public static final Biome WARM_OCEAN;
    public static final Biome LUKEWARM_OCEAN;
    public static final Biome COLD_OCEAN;
    public static final Biome DEEP_WARM_OCEAN;
    public static final Biome DEEP_LUKEWARM_OCEAN;
    public static final Biome DEEP_COLD_OCEAN;
    public static final Biome DEEP_FROZEN_OCEAN;
    public static final Biome THE_VOID;
    public static final Biome SUNFLOWER_PLAINS;
    public static final Biome DESERT_LAKES;
    public static final Biome GRAVELLY_MOUNTAINS;
    public static final Biome FLOWER_FOREST;
    public static final Biome TAIGA_MOUNTAINS;
    public static final Biome SWAMP_HILLS;
    public static final Biome ICE_SPIKES;
    public static final Biome MODIFIED_JUNGLE;
    public static final Biome MODIFIED_JUNGLE_EDGE;
    public static final Biome TALL_BIRCH_FOREST;
    public static final Biome TALL_BIRCH_HILLS;
    public static final Biome DARK_FOREST_HILLS;
    public static final Biome SNOWY_TAIGA_MOUNTAINS;
    public static final Biome GIANT_SPRUCE_TAIGA;
    public static final Biome GIANT_SPRUCE_TAIGA_HILLS;
    public static final Biome MODIFIED_GRAVELLY_MOUNTAINS;
    public static final Biome SHATTERED_SAVANNA;
    public static final Biome SHATTERED_SAVANNA_PLATEAU;
    public static final Biome ERODED_BADLANDS;
    public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU;
    public static final Biome MODIFIED_BADLANDS_PLATEAU;
    public static final Biome BAMBOO_JUNGLE;
    public static final Biome BAMBOO_JUNGLE_HILLS;
    public static final Biome SOUL_SAND_VALLEY;
    public static final Biome CRIMSON_FOREST;
    public static final Biome WARPED_FOREST;
    public static final Biome BASALT_DELTAS;

    private static Biome register(int rawId, String id, Biome biome) {
        BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, id, biome);
        if (biome.hasParent()) {
            MUTATED_BIOMES.set(biome, BuiltinRegistries.BIOME.getRawId(BuiltinRegistries.BIOME.get(new Identifier(biome.parent))));
        }
        return biome;
    }

    @Nullable
    public static Biome getMutated(Biome arg) {
        return MUTATED_BIOMES.get(BuiltinRegistries.BIOME.getRawId(arg));
    }

    static {
        DEFAULT = OCEAN = Biomes.register(0, "ocean", DefaultBiomeCreator.createNormalOcean(false));
        PLAINS = Biomes.register(1, "plains", DefaultBiomeCreator.createPlains(null, false));
        DESERT = Biomes.register(2, "desert", DefaultBiomeCreator.createDesert(null, 0.125f, 0.05f, true, true, true));
        MOUNTAINS = Biomes.register(3, "mountains", DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.MOUNTAIN, false, null));
        FOREST = Biomes.register(4, "forest", DefaultBiomeCreator.createNormalForest(0.1f, 0.2f));
        TAIGA = Biomes.register(5, "taiga", DefaultBiomeCreator.createTaiga(null, 0.2f, 0.2f, false, false, true, false));
        SWAMP = Biomes.register(6, "swamp", DefaultBiomeCreator.createSwamp(null, -0.2f, 0.1f, false));
        RIVER = Biomes.register(7, "river", DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.5f, 4159204, false));
        NETHER_WASTES = Biomes.register(8, "nether_wastes", DefaultBiomeCreator.createNetherWastes());
        THE_END = Biomes.register(9, "the_end", DefaultBiomeCreator.createTheEnd());
        FROZEN_OCEAN = Biomes.register(10, "frozen_ocean", DefaultBiomeCreator.createFrozenOcean(false));
        FROZEN_RIVER = Biomes.register(11, "frozen_river", DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.0f, 3750089, true));
        SNOWY_TUNDRA = Biomes.register(12, "snowy_tundra", DefaultBiomeCreator.createSnowyTundra(null, 0.125f, 0.05f, false, false));
        SNOWY_MOUNTAINS = Biomes.register(13, "snowy_mountains", DefaultBiomeCreator.createSnowyTundra(null, 0.45f, 0.3f, false, true));
        MUSHROOM_FIELDS = Biomes.register(14, "mushroom_fields", DefaultBiomeCreator.createMushroomFields(0.2f, 0.3f));
        MUSHROOM_FIELD_SHORE = Biomes.register(15, "mushroom_field_shore", DefaultBiomeCreator.createMushroomFields(0.0f, 0.025f));
        BEACH = Biomes.register(16, "beach", DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.8f, 0.4f, 4159204, false, false));
        DESERT_HILLS = Biomes.register(17, "desert_hills", DefaultBiomeCreator.createDesert(null, 0.45f, 0.3f, false, true, false));
        WOODED_HILLS = Biomes.register(18, "wooded_hills", DefaultBiomeCreator.createNormalForest(0.45f, 0.3f));
        TAIGA_HILLS = Biomes.register(19, "taiga_hills", DefaultBiomeCreator.createTaiga(null, 0.45f, 0.3f, false, false, false, false));
        MOUNTAIN_EDGE = Biomes.register(20, "mountain_edge", DefaultBiomeCreator.createMountains(0.8f, 0.3f, ConfiguredSurfaceBuilders.GRASS, true, null));
        JUNGLE = Biomes.register(21, "jungle", DefaultBiomeCreator.createJungle());
        JUNGLE_HILLS = Biomes.register(22, "jungle_hills", DefaultBiomeCreator.createJungleHills());
        JUNGLE_EDGE = Biomes.register(23, "jungle_edge", DefaultBiomeCreator.createJungleEdge());
        DEEP_OCEAN = Biomes.register(24, "deep_ocean", DefaultBiomeCreator.createNormalOcean(true));
        STONE_SHORE = Biomes.register(25, "stone_shore", DefaultBiomeCreator.createBeach(0.1f, 0.8f, 0.2f, 0.3f, 4159204, false, true));
        SNOWY_BEACH = Biomes.register(26, "snowy_beach", DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.05f, 0.3f, 4020182, true, false));
        BIRCH_FOREST = Biomes.register(27, "birch_forest", DefaultBiomeCreator.createBirchForest(0.1f, 0.2f, null, false));
        BIRCH_FOREST_HILLS = Biomes.register(28, "birch_forest_hills", DefaultBiomeCreator.createBirchForest(0.45f, 0.3f, null, false));
        DARK_FOREST = Biomes.register(29, "dark_forest", DefaultBiomeCreator.createDarkForest(null, 0.1f, 0.2f, false));
        SNOWY_TAIGA = Biomes.register(30, "snowy_taiga", DefaultBiomeCreator.createTaiga(null, 0.2f, 0.2f, true, false, false, true));
        SNOWY_TAIGA_HILLS = Biomes.register(31, "snowy_taiga_hills", DefaultBiomeCreator.createTaiga(null, 0.45f, 0.3f, true, false, false, false));
        GIANT_TREE_TAIGA = Biomes.register(32, "giant_tree_taiga", DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.3f, false, null));
        GIANT_TREE_TAIGA_HILLS = Biomes.register(33, "giant_tree_taiga_hills", DefaultBiomeCreator.createGiantTreeTaiga(0.45f, 0.3f, 0.3f, false, null));
        WOODED_MOUNTAINS = Biomes.register(34, "wooded_mountains", DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRASS, true, null));
        SAVANNA = Biomes.register(35, "savanna", DefaultBiomeCreator.createSavanna(null, 0.125f, 0.05f, 1.2f, false, false));
        SAVANNA_PLATEAU = Biomes.register(36, "savanna_plateau", DefaultBiomeCreator.createSavannaPlateau());
        BADLANDS = Biomes.register(37, "badlands", DefaultBiomeCreator.createNormalBadlands(null, 0.1f, 0.2f, false));
        WOODED_BADLANDS_PLATEAU = Biomes.register(38, "wooded_badlands_plateau", DefaultBiomeCreator.createWoodedBadlandsPlateau(null, 1.5f, 0.025f));
        BADLANDS_PLATEAU = Biomes.register(39, "badlands_plateau", DefaultBiomeCreator.createNormalBadlands(null, 1.5f, 0.025f, true));
        SMALL_END_ISLANDS = Biomes.register(40, "small_end_islands", DefaultBiomeCreator.createSmallEndIslands());
        END_MIDLANDS = Biomes.register(41, "end_midlands", DefaultBiomeCreator.createEndMidlands());
        END_HIGHLANDS = Biomes.register(42, "end_highlands", DefaultBiomeCreator.createEndHighlands());
        END_BARRENS = Biomes.register(43, "end_barrens", DefaultBiomeCreator.createEndBarrens());
        WARM_OCEAN = Biomes.register(44, "warm_ocean", DefaultBiomeCreator.createWarmOcean());
        LUKEWARM_OCEAN = Biomes.register(45, "lukewarm_ocean", DefaultBiomeCreator.createLukewarmOcean(false));
        COLD_OCEAN = Biomes.register(46, "cold_ocean", DefaultBiomeCreator.createColdOcean(false));
        DEEP_WARM_OCEAN = Biomes.register(47, "deep_warm_ocean", DefaultBiomeCreator.createDeepWarmOcean());
        DEEP_LUKEWARM_OCEAN = Biomes.register(48, "deep_lukewarm_ocean", DefaultBiomeCreator.createLukewarmOcean(true));
        DEEP_COLD_OCEAN = Biomes.register(49, "deep_cold_ocean", DefaultBiomeCreator.createColdOcean(true));
        DEEP_FROZEN_OCEAN = Biomes.register(50, "deep_frozen_ocean", DefaultBiomeCreator.createFrozenOcean(true));
        THE_VOID = Biomes.register(127, "the_void", DefaultBiomeCreator.createTheVoid());
        SUNFLOWER_PLAINS = Biomes.register(129, "sunflower_plains", DefaultBiomeCreator.createPlains("plains", true));
        DESERT_LAKES = Biomes.register(130, "desert_lakes", DefaultBiomeCreator.createDesert("desert", 0.225f, 0.25f, false, false, false));
        GRAVELLY_MOUNTAINS = Biomes.register(131, "gravelly_mountains", DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false, "mountains"));
        FLOWER_FOREST = Biomes.register(132, "flower_forest", DefaultBiomeCreator.createFlowerForest());
        TAIGA_MOUNTAINS = Biomes.register(133, "taiga_mountains", DefaultBiomeCreator.createTaiga("taiga", 0.3f, 0.4f, false, true, false, false));
        SWAMP_HILLS = Biomes.register(134, "swamp_hills", DefaultBiomeCreator.createSwamp("swamp", -0.1f, 0.3f, true));
        ICE_SPIKES = Biomes.register(140, "ice_spikes", DefaultBiomeCreator.createSnowyTundra("snowy_tundra", 0.425f, 0.45000002f, true, false));
        MODIFIED_JUNGLE = Biomes.register(149, "modified_jungle", DefaultBiomeCreator.createModifiedJungle());
        MODIFIED_JUNGLE_EDGE = Biomes.register(151, "modified_jungle_edge", DefaultBiomeCreator.createModifiedJungleEdge());
        TALL_BIRCH_FOREST = Biomes.register(155, "tall_birch_forest", DefaultBiomeCreator.createBirchForest(0.2f, 0.4f, "birch_forest", true));
        TALL_BIRCH_HILLS = Biomes.register(156, "tall_birch_hills", DefaultBiomeCreator.createBirchForest(0.55f, 0.5f, "birch_forest_hills", true));
        DARK_FOREST_HILLS = Biomes.register(157, "dark_forest_hills", DefaultBiomeCreator.createDarkForest("dark_forest", 0.2f, 0.4f, true));
        SNOWY_TAIGA_MOUNTAINS = Biomes.register(158, "snowy_taiga_mountains", DefaultBiomeCreator.createTaiga("snowy_taiga", 0.3f, 0.4f, true, true, false, false));
        GIANT_SPRUCE_TAIGA = Biomes.register(160, "giant_spruce_taiga", DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true, "giant_tree_taiga"));
        GIANT_SPRUCE_TAIGA_HILLS = Biomes.register(161, "giant_spruce_taiga_hills", DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true, "giant_tree_taiga_hills"));
        MODIFIED_GRAVELLY_MOUNTAINS = Biomes.register(162, "modified_gravelly_mountains", DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false, "wooded_mountains"));
        SHATTERED_SAVANNA = Biomes.register(163, "shattered_savanna", DefaultBiomeCreator.createSavanna("savanna", 0.3625f, 1.225f, 1.1f, true, true));
        SHATTERED_SAVANNA_PLATEAU = Biomes.register(164, "shattered_savanna_plateau", DefaultBiomeCreator.createSavanna("savanna_plateau", 1.05f, 1.2125001f, 1.0f, true, true));
        ERODED_BADLANDS = Biomes.register(165, "eroded_badlands", DefaultBiomeCreator.createErodedBadlands());
        MODIFIED_WOODED_BADLANDS_PLATEAU = Biomes.register(166, "modified_wooded_badlands_plateau", DefaultBiomeCreator.createWoodedBadlandsPlateau("wooded_badlands_plateau", 0.45f, 0.3f));
        MODIFIED_BADLANDS_PLATEAU = Biomes.register(167, "modified_badlands_plateau", DefaultBiomeCreator.createNormalBadlands("badlands_plateau", 0.45f, 0.3f, true));
        BAMBOO_JUNGLE = Biomes.register(168, "bamboo_jungle", DefaultBiomeCreator.createNormalBambooJungle());
        BAMBOO_JUNGLE_HILLS = Biomes.register(169, "bamboo_jungle_hills", DefaultBiomeCreator.createBambooJungleHills());
        SOUL_SAND_VALLEY = Biomes.register(170, "soul_sand_valley", DefaultBiomeCreator.createSoulSandValley());
        CRIMSON_FOREST = Biomes.register(171, "crimson_forest", DefaultBiomeCreator.createCrimsonForest());
        WARPED_FOREST = Biomes.register(172, "warped_forest", DefaultBiomeCreator.createWarpedForest());
        BASALT_DELTAS = Biomes.register(173, "basalt_deltas", DefaultBiomeCreator.createBasaltDeltas());
        Collections.addAll(Biome.BIOMES, OCEAN, PLAINS, DESERT, MOUNTAINS, FOREST, TAIGA, SWAMP, RIVER, FROZEN_RIVER, SNOWY_TUNDRA, SNOWY_MOUNTAINS, MUSHROOM_FIELDS, MUSHROOM_FIELD_SHORE, BEACH, DESERT_HILLS, WOODED_HILLS, TAIGA_HILLS, JUNGLE, JUNGLE_HILLS, JUNGLE_EDGE, DEEP_OCEAN, STONE_SHORE, SNOWY_BEACH, BIRCH_FOREST, BIRCH_FOREST_HILLS, DARK_FOREST, SNOWY_TAIGA, SNOWY_TAIGA_HILLS, GIANT_TREE_TAIGA, GIANT_TREE_TAIGA_HILLS, WOODED_MOUNTAINS, SAVANNA, SAVANNA_PLATEAU, BADLANDS, WOODED_BADLANDS_PLATEAU, BADLANDS_PLATEAU);
    }
}

