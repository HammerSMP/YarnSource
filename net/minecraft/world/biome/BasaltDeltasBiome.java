/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.class_5463;
import net.minecraft.class_5464;
import net.minecraft.class_5470;
import net.minecraft.class_5471;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class BasaltDeltasBiome
extends Biome {
    public BasaltDeltasBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.BASALT_DELTAS).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(4341314).fogColor(6840176).particleConfig(new BiomeParticleConfig(ParticleTypes.WHITE_ASH, 0.118093334f)).loopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111)).music(MusicType.method_27283(SoundEvents.MUSIC_NETHER_BASALT_DELTAS)).build()).parent(null));
        this.addStructureFeature(class_5470.RUINED_PORTAL_NETHER);
        this.addCarver(GenerationStep.Carver.AIR, class_5463.NETHER_CAVE);
        this.addStructureFeature(class_5470.FORTRESS);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, class_5464.DELTA);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SPRING_LAVA_DOUBLE);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, class_5464.SMALL_BASALT_COLUMNS);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, class_5464.LARGE_BASALT_COLUMNS);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.BASALT_BLOBS);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.BLACKSTONE_BLOBS);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.SPRING_DELTA);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.PATCH_FIRE);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.PATCH_SOUL_FIRE);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.GLOWSTONE_EXTRA);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.GLOWSTONE);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.BROWN_MUSHROOM_NETHER);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.RED_MUSHROOM_NETHER);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_MAGMA);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.SPRING_CLOSED_DOUBLE);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_GOLD_DELTAS);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_QUARTZ_DELTAS);
        DefaultBiomeFeatures.addAncientDebris(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 40, 1, 1));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
    }
}

