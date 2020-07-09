/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.class_5464;
import net.minecraft.class_5470;
import net.minecraft.class_5471;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class DeepWarmOceanBiome
extends Biome {
    public DeepWarmOceanBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.FULL_SAND).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.OCEAN).depth(-1.8f).scale(0.1f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4445678).waterFogColor(270131).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(class_5470.OCEAN_RUIN_WARM);
        this.addStructureFeature(class_5470.MONUMENT);
        DefaultBiomeFeatures.addOceanStructures(this);
        this.addStructureFeature(class_5470.RUINED_PORTAL_OCEAN);
        DefaultBiomeFeatures.addOceanCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SEAGRASS_DEEP_WARM);
        DefaultBiomeFeatures.addSeagrassOnStone(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 5, 1, 4));
        this.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
        this.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
        DefaultBiomeFeatures.method_30581(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
    }
}

