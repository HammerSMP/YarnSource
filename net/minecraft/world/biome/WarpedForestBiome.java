/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class WarpedForestBiome
extends Biome {
    protected WarpedForestBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.NETHER_FOREST, SurfaceBuilder.WARPED_NYLIUM_CONFIG).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(1705242).particleConfig(new BiomeParticleConfig(ParticleTypes.WARPED_SPORE, 0.01428f)).loopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP).moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0)).additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111)).music(MusicType.method_27283(SoundEvents.MUSIC_NETHER_WARPED_FOREST)).build()).parent(null).noises((List<Biome.MixedNoisePoint>)ImmutableList.of((Object)new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.375f))));
        this.addStructureFeature(DefaultBiomeFeatures.field_24701);
        this.addStructureFeature(DefaultBiomeFeatures.field_24705);
        this.addStructureFeature(DefaultBiomeFeatures.field_24686);
        this.addCarver(GenerationStep.Carver.AIR, WarpedForestBiome.configureCarver(Carver.NETHER_CAVE, new ProbabilityConfig(0.2f)));
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.SPRING_FEATURE.configure(DefaultBiomeFeatures.LAVA_SPRING_CONFIG).createDecoratedFeature(Decorator.COUNT_VERY_BIASED_RANGE.configure(new RangeDecoratorConfig(20, 8, 16, 256))));
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.SPRING_FEATURE.configure(DefaultBiomeFeatures.NETHER_SPRING_CONFIG).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 4, 8, 128))));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.NETHER_FIRE_CONFIG).createDecoratedFeature(Decorator.FIRE.configure(new CountDecoratorConfig(10))));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.SOUL_FIRE_CONFIG).createDecoratedFeature(Decorator.FIRE.configure(new CountDecoratorConfig(10))));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.LIGHT_GEM_CHANCE.configure(new CountDecoratorConfig(10))));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 128))));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.MAGMA_BLOCK.getDefaultState(), 33)).createDecoratedFeature(Decorator.MAGMA.configure(new CountDecoratorConfig(4))));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.SPRING_FEATURE.configure(DefaultBiomeFeatures.ENCLOSED_NETHER_SPRING_CONFIG).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(16, 10, 20, 128))));
        DefaultBiomeFeatures.addWarpedForestVegetation(this);
        DefaultBiomeFeatures.addNetherOres(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 2, 4));
        this.addSpawnDensity(EntityType.ENDERMAN, 1.0, 0.08);
    }
}

