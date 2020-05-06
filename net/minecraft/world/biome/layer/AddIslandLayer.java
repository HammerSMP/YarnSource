/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddIslandLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource arg, int i, int j, int k, int l, int m) {
        if (BiomeLayers.isShallowOcean(m) && BiomeLayers.isShallowOcean(i) && BiomeLayers.isShallowOcean(j) && BiomeLayers.isShallowOcean(l) && BiomeLayers.isShallowOcean(k) && arg.nextInt(2) == 0) {
            return 1;
        }
        return m;
    }
}

