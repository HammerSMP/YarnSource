/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum SimpleLandNoiseLayer implements IdentitySamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource arg, int i) {
        return BiomeLayers.isShallowOcean(i) ? i : arg.nextInt(299999) + 2;
    }
}

