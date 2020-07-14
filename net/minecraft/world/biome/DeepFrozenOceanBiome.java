/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class DeepFrozenOceanBiome
extends Biome {
    protected static final OctaveSimplexNoiseSampler noiseSampler = new OctaveSimplexNoiseSampler(new ChunkRandom(3456L), (List<Integer>)ImmutableList.of((Object)-2, (Object)-1, (Object)0));

    public DeepFrozenOceanBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.FROZEN_OCEAN).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.OCEAN).depth(-1.8f).scale(0.1f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
        this.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
        DefaultBiomeFeatures.addOceanStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
        DefaultBiomeFeatures.addOceanCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addIcebergs(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addBlueIce(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 1, 1, 4));
        this.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
        DefaultBiomeFeatures.addBatsAndMonsters(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
    }

    @Override
    protected float computeTemperature(BlockPos blockPos) {
        double h;
        double e;
        float f = this.getTemperature();
        double d = noiseSampler.sample((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05, false) * 7.0;
        double g = d + (e = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2, false));
        if (g < 0.3 && (h = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09, false)) < 0.8) {
            f = 0.2f;
        }
        if (blockPos.getY() > 64) {
            float i = (float)(TEMPERATURE_NOISE.sample((float)blockPos.getX() / 8.0f, (float)blockPos.getZ() / 8.0f, false) * 4.0);
            return f - (i + (float)blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return f;
    }
}

