/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddColdClimatesLayer implements SouthEastSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource arg, int i) {
        if (BiomeLayers.isShallowOcean(i)) {
            return i;
        }
        int j = arg.nextInt(6);
        if (j == 0) {
            return 4;
        }
        if (j == 1) {
            return 3;
        }
        return 1;
    }
}

