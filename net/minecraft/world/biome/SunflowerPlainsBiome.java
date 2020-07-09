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

public final class SunflowerPlainsBiome
extends Biome {
    public SunflowerPlainsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.PLAINS).depth(0.125f).scale(0.05f).temperature(0.8f).downfall(0.4f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent("plains"));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(class_5470.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addPlainsTallGrass(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_SUNFLOWER);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addPlainsFeatures(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_SUGAR_CANE);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.PATCH_PUMPKIN);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.method_30580(this);
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.HORSE, 5, 2, 6));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.DONKEY, 1, 1, 3));
        DefaultBiomeFeatures.method_30581(this);
    }
}

