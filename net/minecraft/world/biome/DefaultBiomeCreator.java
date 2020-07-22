/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.BadlandsBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.DarkForestBiome;
import net.minecraft.world.biome.FrozenOceanBiome;
import net.minecraft.world.biome.SnowyTundraBiome;
import net.minecraft.world.biome.SwampBiome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class DefaultBiomeCreator {
    public static Biome createGiantTreeTaiga(float depth, float scale, float temperature, boolean spruce, @Nullable String parent) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GIANT_TREE_TAIGA).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.TAIGA).depth(depth).scale(scale).temperature(temperature).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMossyRocks(lv);
        DefaultBiomeFeatures.addLargeFerns(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, spruce ? ConfiguredFeatures.TREES_GIANT_SPRUCE : ConfiguredFeatures.TREES_GIANT);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addGiantTaigaGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addSweetBerryBushes(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
        if (spruce) {
            DefaultBiomeFeatures.addBatsAndMonsters(lv);
        } else {
            DefaultBiomeFeatures.addBats(lv);
            DefaultBiomeFeatures.addMonsters(lv, 100, 25, 100);
        }
        return lv;
    }

    public static Biome createBirchForest(float depth, float scale, @Nullable String parent, boolean tallTrees) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.6f).downfall(0.6f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addForestFlowers(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        if (tallTrees) {
            DefaultBiomeFeatures.addTallBirchTrees(lv);
        } else {
            DefaultBiomeFeatures.addBirchTrees(lv);
        }
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addForestGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createJungle() {
        return DefaultBiomeCreator.createJungle(0.1f, 0.2f, 40, 2, 3);
    }

    public static Biome createJungleEdge() {
        return DefaultBiomeCreator.createJungleFeatures(null, 0.1f, 0.2f, 0.8f, false, true, false);
    }

    public static Biome createModifiedJungleEdge() {
        return DefaultBiomeCreator.createJungleFeatures("jungle_edge", 0.2f, 0.4f, 0.8f, false, true, true);
    }

    public static Biome createModifiedJungle() {
        Biome lv = DefaultBiomeCreator.createJungleFeatures("jungle", 0.2f, 0.4f, 0.9f, false, false, true);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PARROT, 10, 1, 1));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
        return lv;
    }

    public static Biome createJungleHills() {
        return DefaultBiomeCreator.createJungle(0.45f, 0.3f, 10, 1, 1);
    }

    public static Biome createNormalBambooJungle() {
        return DefaultBiomeCreator.createBambooJungle(0.1f, 0.2f, 40, 2);
    }

    public static Biome createBambooJungleHills() {
        return DefaultBiomeCreator.createBambooJungle(0.45f, 0.3f, 10, 1);
    }

    private static Biome createJungle(float depth, float scale, int parrotWeight, int parrotMaxGroupSize, int ocelotMaxGroupSize) {
        Biome lv = DefaultBiomeCreator.createJungleFeatures(null, depth, scale, 0.9f, false, false, false);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PARROT, parrotWeight, 1, parrotMaxGroupSize));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.OCELOT, 2, 1, ocelotMaxGroupSize));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PANDA, 1, 1, 2));
        return lv;
    }

    private static Biome createBambooJungle(float depth, float scale, int parrotWeight, int parrotMaxGroupSize) {
        Biome lv = DefaultBiomeCreator.createJungleFeatures(null, depth, scale, 0.9f, true, false, false);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PARROT, parrotWeight, 1, parrotMaxGroupSize));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PANDA, 80, 1, 2));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
        return lv;
    }

    private static Biome createJungleFeatures(@Nullable String parent, float depth, float scale, float downfall, boolean bambooTrees, boolean edge, boolean modified) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.JUNGLE).depth(depth).scale(scale).temperature(0.95f).downfall(downfall).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        if (!edge && !modified) {
            lv.addStructureFeature(ConfiguredStructureFeatures.JUNGLE_PYRAMID);
        }
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_JUNGLE);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        if (bambooTrees) {
            DefaultBiomeFeatures.addBambooJungleTrees(lv);
        } else {
            if (!edge && !modified) {
                DefaultBiomeFeatures.addBamboo(lv);
            }
            if (edge) {
                DefaultBiomeFeatures.addJungleEdgeTrees(lv);
            } else {
                DefaultBiomeFeatures.addJungleTrees(lv);
            }
        }
        DefaultBiomeFeatures.addExtraDefaultFlowers(lv);
        DefaultBiomeFeatures.addJungleGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addJungleVegetation(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addJungleMobs(lv);
        return lv;
    }

    public static Biome createMountains(float depth, float scale, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder, boolean extraTrees, @Nullable String parent) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(surfaceBuilder).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.EXTREME_HILLS).depth(depth).scale(scale).temperature(0.2f).downfall(0.3f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        if (extraTrees) {
            DefaultBiomeFeatures.addExtraMountainTrees(lv);
        } else {
            DefaultBiomeFeatures.addMountainTrees(lv);
        }
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addDefaultGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addEmeraldOre(lv);
        DefaultBiomeFeatures.addInfestedStone(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.LLAMA, 5, 4, 6));
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createDesert(@Nullable String parent, float depth, float scale, boolean illagerStructures, boolean pyramids, boolean fossils) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.DESERT).precipitation(Biome.Precipitation.NONE).category(Biome.Category.DESERT).depth(depth).scale(scale).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        if (illagerStructures) {
            lv.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_DESERT);
            lv.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
        }
        if (pyramids) {
            lv.addStructureFeature(ConfiguredStructureFeatures.DESERT_PYRAMID);
        }
        if (fossils) {
            DefaultBiomeFeatures.addFossils(lv);
        }
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_DESERT);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDesertLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addDefaultGrass(lv);
        DefaultBiomeFeatures.addDesertDeadBushes(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDesertVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addDesertFeatures(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addDesertMobs(lv);
        return lv;
    }

    public static Biome createPlains(@Nullable String parent, boolean sunflower) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.PLAINS).depth(0.125f).scale(0.05f).temperature(0.8f).downfall(0.4f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        if (!sunflower) {
            lv.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_PLAINS);
            lv.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
        }
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addPlainsTallGrass(lv);
        if (sunflower) {
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUNFLOWER);
        }
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addPlainsFeatures(lv);
        if (sunflower) {
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE);
        }
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        if (sunflower) {
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
        } else {
            DefaultBiomeFeatures.addDefaultVegetation(lv);
        }
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addPlainsMobs(lv);
        return lv;
    }

    public static Biome createEndBarrens() {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.END).precipitation(Biome.Precipitation.NONE).category(Biome.Category.THEEND).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).method_30637(0).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(0xA080A0).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        DefaultBiomeFeatures.addEndMobs(lv);
        return lv;
    }

    public static Biome createTheEnd() {
        Biome lv = DefaultBiomeCreator.createEndBarrens();
        lv.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_SPIKE);
        return lv;
    }

    public static Biome createEndMidlands() {
        Biome lv = DefaultBiomeCreator.createEndBarrens();
        lv.addStructureFeature(ConfiguredStructureFeatures.END_CITY);
        return lv;
    }

    public static Biome createEndHighlands() {
        Biome lv = DefaultBiomeCreator.createEndMidlands();
        lv.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_GATEWAY);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CHORUS_PLANT);
        return lv;
    }

    public static Biome createSmallEndIslands() {
        Biome lv = DefaultBiomeCreator.createEndBarrens();
        lv.addFeature(GenerationStep.Feature.RAW_GENERATION, ConfiguredFeatures.END_ISLAND_DECORATED);
        return lv;
    }

    public static Biome createMushroomFields(float depth, float scale) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.MYCELIUM).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.MUSHROOM).depth(depth).scale(scale).temperature(0.9f).downfall(1.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addMushroomFieldsFeatures(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addMushroomMobs(lv);
        return lv;
    }

    public static Biome createSavanna(@Nullable String parent, float depth, float scale, float temperature, boolean tall, boolean shattered) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(shattered ? ConfiguredSurfaceBuilders.SHATTERED_SAVANNA : ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.NONE).category(Biome.Category.SAVANNA).depth(depth).scale(scale).temperature(temperature).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        if (!tall && !shattered) {
            lv.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_SAVANNA);
            lv.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
        }
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(tall ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        if (!shattered) {
            DefaultBiomeFeatures.addSavannaTallGrass(lv);
        }
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        if (shattered) {
            DefaultBiomeFeatures.addExtraSavannaTrees(lv);
            DefaultBiomeFeatures.addDefaultFlowers(lv);
            DefaultBiomeFeatures.addShatteredSavannaGrass(lv);
        } else {
            DefaultBiomeFeatures.addSavannaTrees(lv);
            DefaultBiomeFeatures.addExtraDefaultFlowers(lv);
            DefaultBiomeFeatures.addSavannaGrass(lv);
        }
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.HORSE, 1, 2, 6));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.DONKEY, 1, 1, 1));
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createSavannaPlateau() {
        Biome lv = DefaultBiomeCreator.createSavanna(null, 1.5f, 0.025f, 1.0f, true, false);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.LLAMA, 8, 4, 4));
        return lv;
    }

    private static Biome createBadlands(@Nullable String parent, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder, float depth, float scale, boolean plateau, boolean trees) {
        BadlandsBiome lv = new BadlandsBiome(new Biome.Settings().surfaceBuilder(surfaceBuilder).precipitation(Biome.Precipitation.NONE).category(Biome.Category.MESA).depth(depth).scale(scale).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        DefaultBiomeFeatures.addBadlandsUndergroundStructures(lv);
        lv.addStructureFeature(plateau ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addExtraGoldOre(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        if (trees) {
            DefaultBiomeFeatures.addBadlandsPlateauTrees(lv);
        }
        DefaultBiomeFeatures.addBadlandsGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addBadlandsVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createNormalBadlands(@Nullable String parent, float depth, float scale, boolean plateau) {
        return DefaultBiomeCreator.createBadlands(parent, ConfiguredSurfaceBuilders.BADLANDS, depth, scale, plateau, false);
    }

    public static Biome createWoodedBadlandsPlateau(@Nullable String parent, float depth, float scale) {
        return DefaultBiomeCreator.createBadlands(parent, ConfiguredSurfaceBuilders.WOODED_BADLANDS, depth, scale, true, true);
    }

    public static Biome createErodedBadlands() {
        return DefaultBiomeCreator.createBadlands("badlands", ConfiguredSurfaceBuilders.ERODED_BADLANDS, 0.1f, 0.2f, true, false);
    }

    private static Biome createOcean(ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder, int waterColor, int waterFogColor, boolean deep, boolean warm, boolean preserveOldStructureOrder) {
        ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> lv2;
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(surfaceBuilder).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.OCEAN).depth(deep ? -1.8f : -1.0f).scale(0.1f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(waterColor).waterFogColor(waterFogColor).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> configuredStructureFeature = lv2 = warm ? ConfiguredStructureFeatures.OCEAN_RUIN_WARM : ConfiguredStructureFeatures.OCEAN_RUIN_COLD;
        if (preserveOldStructureOrder) {
            if (deep) {
                lv.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
            }
            DefaultBiomeFeatures.addOceanStructures(lv);
            lv.addStructureFeature(lv2);
        } else {
            lv.addStructureFeature(lv2);
            if (deep) {
                lv.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
            }
            DefaultBiomeFeatures.addOceanStructures(lv);
        }
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
        DefaultBiomeFeatures.addOceanCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addDefaultGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        return lv;
    }

    public static Biome createColdOcean(boolean deep) {
        Biome lv = DefaultBiomeCreator.createOcean(ConfiguredSurfaceBuilders.GRASS, 4020182, 329011, deep, false, !deep);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP_COLD : ConfiguredFeatures.SEAGRASS_COLD);
        DefaultBiomeFeatures.addSeagrassOnStone(lv);
        DefaultBiomeFeatures.addKelp(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addOceanMobs(lv, 3, 4, 15);
        lv.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
        return lv;
    }

    public static Biome createNormalOcean(boolean deep) {
        Biome lv = DefaultBiomeCreator.createOcean(ConfiguredSurfaceBuilders.GRASS, 4159204, 329011, deep, false, true);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP : ConfiguredFeatures.SEAGRASS_NORMAL);
        DefaultBiomeFeatures.addSeagrassOnStone(lv);
        DefaultBiomeFeatures.addKelp(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addOceanMobs(lv, 1, 4, 10);
        lv.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.DOLPHIN, 1, 1, 2));
        return lv;
    }

    public static Biome createLukewarmOcean(boolean deep) {
        Biome lv = DefaultBiomeCreator.createOcean(ConfiguredSurfaceBuilders.OCEAN_SAND, 4566514, 267827, deep, true, false);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP_WARM : ConfiguredFeatures.SEAGRASS_WARM);
        if (deep) {
            DefaultBiomeFeatures.addSeagrassOnStone(lv);
        }
        DefaultBiomeFeatures.addLessKelp(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        if (deep) {
            DefaultBiomeFeatures.addOceanMobs(lv, 8, 4, 8);
        } else {
            DefaultBiomeFeatures.addOceanMobs(lv, 10, 2, 15);
        }
        lv.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.PUFFERFISH, 5, 1, 3));
        lv.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
        lv.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
        return lv;
    }

    public static Biome createWarmOcean() {
        Biome lv = DefaultBiomeCreator.createOcean(ConfiguredSurfaceBuilders.FULL_SAND, 4445678, 270131, false, true, false);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARM_OCEAN_VEGETATION);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_WARM);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEA_PICKLE);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        lv.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
        DefaultBiomeFeatures.addWarmOceanMobs(lv, 10, 4);
        return lv;
    }

    public static Biome createDeepWarmOcean() {
        Biome lv = DefaultBiomeCreator.createOcean(ConfiguredSurfaceBuilders.FULL_SAND, 4445678, 270131, true, true, false);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_DEEP_WARM);
        DefaultBiomeFeatures.addSeagrassOnStone(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addWarmOceanMobs(lv, 5, 1);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
        return lv;
    }

    public static Biome createFrozenOcean(boolean monument) {
        FrozenOceanBiome lv = new FrozenOceanBiome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.FROZEN_OCEAN).precipitation(monument ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW).category(Biome.Category.OCEAN).depth(monument ? -1.8f : -1.0f).scale(0.1f).temperature(monument ? 0.5f : 0.0f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        lv.addStructureFeature(ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
        if (monument) {
            lv.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
        }
        DefaultBiomeFeatures.addOceanStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
        DefaultBiomeFeatures.addOceanCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addIcebergs(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addBlueIce(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addDefaultGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        lv.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 1, 1, 4));
        lv.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
        return lv;
    }

    private static Biome createForest(@Nullable String parent, float depth, float scale, boolean flower) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.7f).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        if (flower) {
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_VEGETATION_COMMON);
        } else {
            DefaultBiomeFeatures.addForestFlowers(lv);
        }
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        if (flower) {
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_TREES);
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_FOREST);
            DefaultBiomeFeatures.addDefaultGrass(lv);
        } else {
            DefaultBiomeFeatures.addForestTrees(lv);
            DefaultBiomeFeatures.addDefaultFlowers(lv);
            DefaultBiomeFeatures.addForestGrass(lv);
        }
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createNormalForest(float depth, float scale) {
        Biome lv = DefaultBiomeCreator.createForest(null, depth, scale, false);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 5, 4, 4));
        return lv;
    }

    public static Biome createFlowerForest() {
        Biome lv = DefaultBiomeCreator.createForest("forest", 0.1f, 0.4f, true);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        return lv;
    }

    public static Biome createTaiga(@Nullable String parent, float depth, float scale, boolean snowy, boolean mountains, boolean villages, boolean igloos) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN).category(Biome.Category.TAIGA).depth(depth).scale(scale).temperature(snowy ? -0.5f : 0.25f).downfall(snowy ? 0.4f : 0.8f).effects(new BiomeEffects.Builder().waterColor(snowy ? 4020182 : 4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        if (villages) {
            lv.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_TAIGA);
            lv.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
        }
        if (igloos) {
            lv.addStructureFeature(ConfiguredStructureFeatures.IGLOO);
        }
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(mountains ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addLargeFerns(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addTaigaTrees(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addTaigaGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        if (snowy) {
            DefaultBiomeFeatures.addSweetBerryBushesSnowy(lv);
        } else {
            DefaultBiomeFeatures.addSweetBerryBushes(lv);
        }
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createDarkForest(@Nullable String parent, float depth, float scale, boolean redMushrooms) {
        DarkForestBiome lv = new DarkForestBiome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.7f).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        lv.addStructureFeature(ConfiguredStructureFeatures.MANSION);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, redMushrooms ? ConfiguredFeatures.DARK_FOREST_VEGETATION_RED : ConfiguredFeatures.DARK_FOREST_VEGETATION_BROWN);
        DefaultBiomeFeatures.addForestFlowers(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addForestGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createSwamp(@Nullable String parent, float depth, float scale, boolean hills) {
        SwampBiome lv = new SwampBiome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.SWAMP).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.SWAMP).depth(depth).scale(scale).temperature(0.8f).downfall(0.9f).effects(new BiomeEffects.Builder().waterColor(6388580).waterFogColor(2302743).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        if (!hills) {
            lv.addStructureFeature(ConfiguredStructureFeatures.SWAMP_HUT);
        }
        lv.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_SWAMP);
        DefaultBiomeFeatures.addLandCarvers(lv);
        if (!hills) {
            DefaultBiomeFeatures.addFossils(lv);
        }
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addClay(lv);
        DefaultBiomeFeatures.addSwampFeatures(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addSwampVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        if (hills) {
            DefaultBiomeFeatures.addFossils(lv);
        } else {
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_SWAMP);
        }
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addFarmAnimals(lv);
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 1, 1, 1));
        return lv;
    }

    public static Biome createSnowyTundra(@Nullable String parent, float depth, float scale, boolean iceSpikes, boolean snowyMountains) {
        SnowyTundraBiome lv = new SnowyTundraBiome(new Biome.Settings().surfaceBuilder(iceSpikes ? ConfiguredSurfaceBuilders.ICE_SPIKES : ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.SNOW).category(Biome.Category.ICY).depth(depth).scale(scale).temperature(0.0f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(parent));
        if (!iceSpikes && !snowyMountains) {
            lv.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_SNOWY);
            lv.addStructureFeature(ConfiguredStructureFeatures.IGLOO);
        }
        DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        if (!iceSpikes && !snowyMountains) {
            lv.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
        }
        lv.addStructureFeature(snowyMountains ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        if (iceSpikes) {
            lv.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_SPIKE);
            lv.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_PATCH);
        }
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addSnowySpruceTrees(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addDefaultGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        DefaultBiomeFeatures.addSnowyMobs(lv);
        return lv;
    }

    public static Biome createRiver(float depth, float scale, float temperature, int waterColor, boolean frozen) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(frozen ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN).category(Biome.Category.RIVER).depth(depth).scale(scale).temperature(temperature).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(waterColor).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        lv.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addDefaultGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        if (!frozen) {
            lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_RIVER);
        }
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        lv.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 2, 1, 4));
        lv.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 5, 1, 5));
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, frozen ? 1 : 100, 1, 1));
        return lv;
    }

    public static Biome createBeach(float depth, float scale, float temperature, float downfall, int waterColor, boolean snowy, boolean stony) {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(stony ? ConfiguredSurfaceBuilders.STONE : ConfiguredSurfaceBuilders.DESERT).precipitation(snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN).category(stony ? Biome.Category.NONE : Biome.Category.BEACH).depth(depth).scale(scale).temperature(temperature).downfall(downfall).effects(new BiomeEffects.Builder().waterColor(waterColor).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        if (stony) {
            DefaultBiomeFeatures.addDefaultUndergroundStructures(lv);
        } else {
            lv.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
            lv.addStructureFeature(ConfiguredStructureFeatures.BURIED_TREASURE);
            lv.addStructureFeature(ConfiguredStructureFeatures.SHIPWRECK_BEACHED);
        }
        lv.addStructureFeature(stony ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(lv);
        DefaultBiomeFeatures.addDefaultLakes(lv);
        DefaultBiomeFeatures.addDungeons(lv);
        DefaultBiomeFeatures.addMineables(lv);
        DefaultBiomeFeatures.addDefaultOres(lv);
        DefaultBiomeFeatures.addDefaultDisks(lv);
        DefaultBiomeFeatures.addDefaultFlowers(lv);
        DefaultBiomeFeatures.addDefaultGrass(lv);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        DefaultBiomeFeatures.addDefaultVegetation(lv);
        DefaultBiomeFeatures.addSprings(lv);
        DefaultBiomeFeatures.addFrozenTopLayer(lv);
        if (!stony && !snowy) {
            lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
        }
        DefaultBiomeFeatures.addBatsAndMonsters(lv);
        return lv;
    }

    public static Biome createTheVoid() {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.NOPE).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NONE).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        lv.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.VOID_START_PLATFORM);
        return lv;
    }

    public static Biome createNetherWastes() {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.NETHER).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(0x330808).loopSound(SoundEvents.AMBIENT_NETHER_WASTES_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111)).music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_NETHER_WASTES)).build()).parent(null));
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
        lv.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
        lv.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
        lv.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NETHER);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NETHER);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
        DefaultBiomeFeatures.addNetherMineables(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.PIGLIN, 15, 4, 4));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
        return lv;
    }

    public static Biome createSoulSandValley() {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.SOUL_SAND_VALLEY).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(1787717).particleConfig(new BiomeParticleConfig(ParticleTypes.ASH, 0.00625f)).loopSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111)).music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY)).build()).parent(null));
        lv.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
        lv.addStructureFeature(ConfiguredStructureFeatures.NETHER_FOSSIL);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
        lv.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
        lv.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
        lv.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.BASALT_PILLAR);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_CRIMSON_ROOTS);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_SOUL_SAND);
        DefaultBiomeFeatures.addNetherMineables(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 20, 5, 5));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
        double d = 0.7;
        double e = 0.15;
        lv.addSpawnDensity(EntityType.SKELETON, 0.7, 0.15);
        lv.addSpawnDensity(EntityType.GHAST, 0.7, 0.15);
        lv.addSpawnDensity(EntityType.ENDERMAN, 0.7, 0.15);
        lv.addSpawnDensity(EntityType.STRIDER, 0.7, 0.15);
        return lv;
    }

    public static Biome createBasaltDeltas() {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.BASALT_DELTAS).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(4341314).fogColor(6840176).particleConfig(new BiomeParticleConfig(ParticleTypes.WHITE_ASH, 0.118093334f)).loopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111)).music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_BASALT_DELTAS)).build()).parent(null));
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
        lv.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
        lv.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
        lv.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.DELTA);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA_DOUBLE);
        lv.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.SMALL_BASALT_COLUMNS);
        lv.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.LARGE_BASALT_COLUMNS);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BASALT_BLOBS);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BLACKSTONE_BLOBS);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_DELTA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NETHER);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NETHER);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED_DOUBLE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GOLD_DELTAS);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_QUARTZ_DELTAS);
        DefaultBiomeFeatures.addAncientDebris(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 40, 1, 1));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
        return lv;
    }

    public static Biome createCrimsonForest() {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.CRIMSON_FOREST).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(0x330303).particleConfig(new BiomeParticleConfig(ParticleTypes.CRIMSON_SPORE, 0.025f)).loopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111)).music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST)).build()).parent(null));
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
        lv.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
        lv.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
        lv.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WEEPING_VINES);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FUNGI);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FOREST_VEGETATION);
        DefaultBiomeFeatures.addNetherMineables(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.HOGLIN, 9, 3, 4));
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.PIGLIN, 5, 3, 4));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
        return lv;
    }

    public static Biome createWarpedForest() {
        Biome lv = new Biome(new Biome.Settings().surfaceBuilder(ConfiguredSurfaceBuilders.WARPED_FOREST).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(1705242).particleConfig(new BiomeParticleConfig(ParticleTypes.WARPED_SPORE, 0.01428f)).loopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111)).music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_WARPED_FOREST)).build()).parent(null));
        lv.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
        lv.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
        lv.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
        lv.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
        DefaultBiomeFeatures.addDefaultMushrooms(lv);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
        lv.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARPED_FUNGI);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARPED_FOREST_VEGETATION);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.NETHER_SPROUTS);
        lv.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TWISTING_VINES);
        DefaultBiomeFeatures.addNetherMineables(lv);
        lv.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
        lv.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
        lv.addSpawnDensity(EntityType.ENDERMAN, 1.0, 0.12);
        return lv;
    }
}

