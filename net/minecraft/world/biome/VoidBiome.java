/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.class_5464;
import net.minecraft.class_5471;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;

public final class VoidBiome
extends Biome {
    public VoidBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.NOPE).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NONE).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, class_5464.VOID_START_PLATFORM);
    }
}

