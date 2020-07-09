/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.class_5470;
import net.minecraft.class_5471;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public final class SnowyBeachBiome
extends Biome {
    public SnowyBeachBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.DESERT).precipitation(Biome.Precipitation.SNOW).category(Biome.Category.BEACH).depth(0.0f).scale(0.025f).temperature(0.05f).downfall(0.3f).effects(new BiomeEffects.Builder().waterColor(4020182).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(class_5470.MINESHAFT);
        this.addStructureFeature(class_5470.BURIED_TREASURE);
        this.addStructureFeature(class_5470.SHIPWRECK_BEACHED);
        this.addStructureFeature(class_5470.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.method_30581(this);
    }
}

