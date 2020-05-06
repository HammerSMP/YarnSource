/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum OceanTemperatureLayer implements InitLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource arg, int i, int j) {
        PerlinNoiseSampler lv = arg.getNoiseSampler();
        double d = lv.sample((double)i / 8.0, (double)j / 8.0, 0.0, 0.0, 0.0);
        if (d > 0.4) {
            return BiomeLayers.WARM_OCEAN_ID;
        }
        if (d > 0.2) {
            return BiomeLayers.LUKEWARM_OCEAN_ID;
        }
        if (d < -0.4) {
            return BiomeLayers.FROZEN_OCEAN_ID;
        }
        if (d < -0.2) {
            return BiomeLayers.COLD_OCEAN_ID;
        }
        return BiomeLayers.OCEAN_ID;
    }
}

