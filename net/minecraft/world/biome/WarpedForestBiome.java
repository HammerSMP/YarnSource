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

public class WarpedForestBiome
extends Biome {
    public WarpedForestBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.WARPED_FOREST).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(1705242).particleConfig(new BiomeParticleConfig(ParticleTypes.WARPED_SPORE, 0.01428f)).loopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111)).music(MusicType.method_27283(SoundEvents.MUSIC_NETHER_WARPED_FOREST)).build()).parent(null));
        this.addStructureFeature(class_5470.FORTRESS);
        this.addStructureFeature(class_5470.BASTION_REMNANT);
        this.addStructureFeature(class_5470.RUINED_PORTAL_NETHER);
        this.addCarver(GenerationStep.Carver.AIR, class_5463.NETHER_CAVE);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SPRING_LAVA);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.SPRING_OPEN);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.PATCH_FIRE);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.PATCH_SOUL_FIRE);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.GLOWSTONE_EXTRA);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.GLOWSTONE);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.ORE_MAGMA);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, class_5464.SPRING_CLOSED);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.WARPED_FUNGI);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.WARPED_FOREST_VEGETATION);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.NETHER_SPROUTS);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.TWISTING_VINES);
        DefaultBiomeFeatures.addNetherMineables(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
        this.addSpawnDensity(EntityType.ENDERMAN, 1.0, 0.12);
    }
}

