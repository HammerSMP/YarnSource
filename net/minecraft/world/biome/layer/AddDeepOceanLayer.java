/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddDeepOceanLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (BiomeLayers.isShallowOcean(center)) {
            int n2 = 0;
            if (BiomeLayers.isShallowOcean(n)) {
                ++n2;
            }
            if (BiomeLayers.isShallowOcean(e)) {
                ++n2;
            }
            if (BiomeLayers.isShallowOcean(w)) {
                ++n2;
            }
            if (BiomeLayers.isShallowOcean(s)) {
                ++n2;
            }
            if (n2 > 3) {
                if (center == BiomeLayers.WARM_OCEAN_ID) {
                    return BiomeLayers.DEEP_WARM_OCEAN_ID;
                }
                if (center == BiomeLayers.LUKEWARM_OCEAN_ID) {
                    return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
                }
                if (center == BiomeLayers.OCEAN_ID) {
                    return BiomeLayers.DEEP_OCEAN_ID;
                }
                if (center == BiomeLayers.COLD_OCEAN_ID) {
                    return BiomeLayers.DEEP_COLD_OCEAN_ID;
                }
                if (center == BiomeLayers.FROZEN_OCEAN_ID) {
                    return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
                }
                return BiomeLayers.DEEP_OCEAN_ID;
            }
        }
        return center;
    }
}

