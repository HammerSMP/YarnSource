/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class SavannaPlateauBiome
extends Biome {
    public SavannaPlateauBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.NONE).category(Biome.Category.SAVANNA).depth(1.5f).scale(0.025f).temperature(1.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addSavannaTallGrass(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addSavannaTrees(this);
        DefaultBiomeFeatures.addExtraDefaultFlowers(this);
        DefaultBiomeFeatures.addSavannaGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.addFarmAnimals(this);
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.HORSE, 1, 2, 6));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.DONKEY, 1, 1, 1));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.LLAMA, 8, 4, 4));
        DefaultBiomeFeatures.addBatsAndMonsters(this);
    }
}

