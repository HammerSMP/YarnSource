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

public final class TaigaBiome
extends Biome {
    public TaigaBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.TAIGA).depth(0.2f).scale(0.2f).temperature(0.25f).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(class_5470.VILLAGE_TAIGA);
        this.addStructureFeature(class_5470.PILLAGER_OUTPOST);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(class_5470.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addLargeFerns(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addTaigaTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addTaigaGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addSweetBerryBushes(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.method_30580(this);
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
        DefaultBiomeFeatures.method_30581(this);
    }
}

