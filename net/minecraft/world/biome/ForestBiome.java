/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.class_5470;
import net.minecraft.class_5471;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public final class ForestBiome
extends Biome {
    public ForestBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).depth(0.1f).scale(0.2f).temperature(0.7f).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(class_5470.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addForestFlowers(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addForestTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addForestGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.method_30580(this);
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 5, 4, 4));
        DefaultBiomeFeatures.method_30581(this);
    }
}

