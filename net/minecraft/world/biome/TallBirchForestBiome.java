/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class TallBirchForestBiome
extends Biome {
    public TallBirchForestBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).depth(0.2f).scale(0.4f).temperature(0.6f).downfall(0.6f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent("birch_forest"));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addForestFlowers(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addTallBirchTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addForestGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.addFarmAnimals(this);
        DefaultBiomeFeatures.addBatsAndMonsters(this);
    }
}

