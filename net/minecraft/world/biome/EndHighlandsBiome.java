/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5464;
import net.minecraft.class_5470;
import net.minecraft.class_5471;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class EndHighlandsBiome
extends Biome {
    public EndHighlandsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.END).precipitation(Biome.Precipitation.NONE).category(Biome.Category.THEEND).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(0xA080A0).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(class_5470.END_CITY);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, class_5464.END_GATEWAY);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.CHORUS_PLANT);
        DefaultBiomeFeatures.method_30587(this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getSkyColor() {
        return 0;
    }
}

