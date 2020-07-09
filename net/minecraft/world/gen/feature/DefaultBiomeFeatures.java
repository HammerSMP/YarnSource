/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.feature;

import net.minecraft.class_5463;
import net.minecraft.class_5464;
import net.minecraft.class_5470;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class DefaultBiomeFeatures {
    public static void addBadlandsUndergroundStructures(Biome arg) {
        arg.addStructureFeature(class_5470.MINESHAFT_MESA);
        arg.addStructureFeature(class_5470.STRONGHOLD);
    }

    public static void addDefaultUndergroundStructures(Biome arg) {
        arg.addStructureFeature(class_5470.MINESHAFT);
        arg.addStructureFeature(class_5470.STRONGHOLD);
    }

    public static void addOceanStructures(Biome arg) {
        arg.addStructureFeature(class_5470.MINESHAFT);
        arg.addStructureFeature(class_5470.SHIPWRECK);
    }

    public static void addLandCarvers(Biome arg) {
        arg.addCarver(GenerationStep.Carver.AIR, class_5463.CAVE);
        arg.addCarver(GenerationStep.Carver.AIR, class_5463.CANYON);
    }

    public static void addOceanCarvers(Biome arg) {
        arg.addCarver(GenerationStep.Carver.AIR, class_5463.OCEAN_CAVE);
        arg.addCarver(GenerationStep.Carver.AIR, class_5463.CANYON);
        arg.addCarver(GenerationStep.Carver.LIQUID, class_5463.UNDERWATER_CANYON);
        arg.addCarver(GenerationStep.Carver.LIQUID, class_5463.UNDERWATER_CAVE);
    }

    public static void addDefaultLakes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LAKES, class_5464.LAKE_WATER);
        arg.addFeature(GenerationStep.Feature.LAKES, class_5464.LAKE_LAVA);
    }

    public static void addDesertLakes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LAKES, class_5464.LAKE_LAVA);
    }

    public static void addDungeons(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, class_5464.MONSTER_ROOM);
    }

    public static void addMineables(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_DIRT);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_GRAVEL);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_GRANITE);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_DIORITE);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_ANDESITE);
    }

    public static void addDefaultOres(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_COAL);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_IRON);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_GOLD);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_REDSTONE);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_DIAMOND);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_LAPIS);
    }

    public static void addExtraGoldOre(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_GOLD_EXTRA);
    }

    public static void addEmeraldOre(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.ORE_EMERALD);
    }

    public static void addInfestedStone(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_INFESTED);
    }

    public static void addDefaultDisks(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.DISK_SAND);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.DISK_CLAY);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.DISK_GRAVEL);
    }

    public static void addClay(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, class_5464.DISK_CLAY);
    }

    public static void addMossyRocks(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, class_5464.FOREST_ROCK);
    }

    public static void addLargeFerns(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_LARGE_FERN);
    }

    public static void addSweetBerryBushesSnowy(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_BERRY_DECORATED);
    }

    public static void addSweetBerryBushes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_BERRY_SPARSE);
    }

    public static void addBamboo(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BAMBOO_LIGHT);
    }

    public static void addBambooJungleTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BAMBOO);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BAMBOO_VEGETATION);
    }

    public static void addTaigaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TAIGA_VEGETATION);
    }

    public static void addWaterBiomeOakTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_WATER);
    }

    public static void addBirchTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BIRCH_BEES_0002);
    }

    public static void addForestTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BIRCH_OTHER);
    }

    public static void addTallBirchTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BIRCH_TALL);
    }

    public static void addSavannaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_SAVANNA);
    }

    public static void addExtraSavannaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_SHATTERED_SAVANNA);
    }

    public static void addMountainTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_MOUNTAIN);
    }

    public static void addExtraMountainTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_MOUNTAIN_EDGE);
    }

    public static void addJungleTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_JUNGLE);
    }

    public static void addJungleEdgeTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_JUNGLE_EDGE);
    }

    public static void addBadlandsPlateauTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.OAK_BADLANDS);
    }

    public static void addSnowySpruceTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SPRUCE_SNOVY);
    }

    public static void addGiantSpruceTaigaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_GIANT_SPRUCE);
    }

    public static void addGiantTreeTaigaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TREES_GIANT);
    }

    public static void addJungleGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_JUNGLE);
    }

    public static void addSavannaTallGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_TALL_GRASS);
    }

    public static void addShatteredSavannaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_NORMAL);
    }

    public static void addSavannaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_SAVANNA);
    }

    public static void addBadlandsGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_BADLANDS);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_DEAD_BUSH_BADLANDS);
    }

    public static void addForestFlowers(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.FOREST_FLOWER_VEGETATION);
    }

    public static void addForestGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_FOREST);
    }

    public static void addSwampFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SWAMP_TREE);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.FLOWER_SWAMP);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_NORMAL);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_DEAD_BUSH);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_WATERLILLY);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BROWN_MUSHROOM_SWAMP);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.RED_MUSHROOM_SWAMP);
    }

    public static void addMushroomFieldsFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.MUSHROOM_FIELD_VEGETATION);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BROWN_MUSHROOM_TAIGA);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.RED_MUSHROOM_TAIGA);
    }

    public static void addPlainsFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PLAIN_VEGETATION);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.FLOWER_PLAIN_DECORATED);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_PLAIN);
    }

    public static void addDesertDeadBushes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_DEAD_BUSH_2);
    }

    public static void addGiantTaigaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_TAIGA);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_DEAD_BUSH);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BROWN_MUSHROOM_GIANT);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.RED_MUSHROOM_GIANT);
    }

    public static void addDefaultFlowers(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.FLOWER_DEFAULT);
    }

    public static void addExtraDefaultFlowers(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.FLOWER_WARM);
    }

    public static void addDefaultGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_BADLANDS);
    }

    public static void addTaigaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_GRASS_TAIGA_2);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BROWN_MUSHROOM_TAIGA);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.RED_MUSHROOM_TAIGA);
    }

    public static void addPlainsTallGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_TALL_GRASS_2);
    }

    public static void addDefaultMushrooms(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.BROWN_MUSHROOM_NORMAL);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.RED_MUSHROOM_NORMAL);
    }

    public static void addDefaultVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_SUGAR_CANE);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_PUMPKIN);
    }

    public static void addBadlandsVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_SUGAR_CANE_BADLANDS);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_PUMPKIN);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_CACTUS_DECORATED);
    }

    public static void addJungleVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_MELON);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.VINES);
    }

    public static void addDesertVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_SUGAR_CANE_DESERT);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_PUMPKIN);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_CACTUS_DESERT);
    }

    public static void addSwampVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_SUGAR_CANE_SWAMP);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_PUMPKIN);
    }

    public static void addDesertFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, class_5464.DESERT_WELL);
    }

    public static void addFossils(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, class_5464.FOSSIL);
    }

    public static void addKelp(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.KELP_COLD);
    }

    public static void addSeagrassOnStone(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SEAGRASS_SIMPLE);
    }

    public static void addLessKelp(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.KELP_WARM);
    }

    public static void addSprings(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SPRING_WATER);
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SPRING_LAVA);
    }

    public static void addIcebergs(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, class_5464.ICEBERG_PACKED);
        arg.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, class_5464.ICEBERG_BLUE);
    }

    public static void addBlueIce(Biome arg) {
        arg.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, class_5464.BLUE_ICE);
    }

    public static void addFrozenTopLayer(Biome arg) {
        arg.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, class_5464.FREEZE_TOP_LAYER);
    }

    public static void addNetherMineables(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_GRAVEL_NETHER);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_BLACKSTONE);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_GOLD_NETHER);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_QUARTZ_NETHER);
        DefaultBiomeFeatures.addAncientDebris(arg);
    }

    public static void addAncientDebris(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_DEBRIS_LARGE);
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_DEBRIS_SMALL);
    }

    public static void method_30580(Biome arg) {
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PIG, 10, 4, 4));
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.COW, 8, 4, 4));
    }

    private static void method_30579(Biome arg) {
        arg.addSpawn(SpawnGroup.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
    }

    public static void method_30581(Biome arg) {
        DefaultBiomeFeatures.method_30579(arg);
        DefaultBiomeFeatures.method_30578(arg, 95, 5, 100);
    }

    public static void method_30582(Biome arg) {
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 10, 2, 3));
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
        DefaultBiomeFeatures.method_30579(arg);
        DefaultBiomeFeatures.method_30578(arg, 95, 5, 20);
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.STRAY, 80, 4, 4));
    }

    public static void method_30583(Biome arg) {
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        DefaultBiomeFeatures.method_30579(arg);
        DefaultBiomeFeatures.method_30578(arg, 19, 1, 100);
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.HUSK, 80, 4, 4));
    }

    public static void method_30584(Biome arg) {
        DefaultBiomeFeatures.method_30580(arg);
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
        DefaultBiomeFeatures.method_30579(arg);
        DefaultBiomeFeatures.method_30578(arg, 100, 25, 100);
    }

    private static void method_30578(Biome arg, int i, int j, int k) {
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, i, 4, 4));
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, j, 1, 1));
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, k, 4, 4));
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
    }

    public static void method_30585(Biome arg) {
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.MOOSHROOM, 8, 4, 8));
        DefaultBiomeFeatures.method_30579(arg);
    }

    public static void method_30586(Biome arg) {
        DefaultBiomeFeatures.method_30580(arg);
        arg.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
        DefaultBiomeFeatures.method_30581(arg);
    }

    public static void method_30587(Biome arg) {
        arg.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
    }
}

