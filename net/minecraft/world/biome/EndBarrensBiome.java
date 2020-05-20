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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndBarrensBiome
extends Biome {
    public EndBarrensBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_CONFIG).precipitation(Biome.Precipitation.NONE).category(Biome.Category.THEEND).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(0xA080A0).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getSkyColor() {
        return 0;
    }
}

