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

public final class SnowyMountainsBiome
extends Biome {
    public SnowyMountainsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.GRASS).precipitation(Biome.Precipitation.SNOW).category(Biome.Category.ICY).depth(0.45f).scale(0.3f).temperature(0.0f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(class_5470.RUINED_PORTAL_MOUNTAIN);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addSnowySpruceTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.method_30582(this);
    }

    @Override
    public float getMaxSpawnChance() {
        return 0.07f;
    }
}

