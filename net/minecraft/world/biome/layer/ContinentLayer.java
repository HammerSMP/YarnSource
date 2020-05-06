/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum ContinentLayer implements InitLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource arg, int i, int j) {
        if (i == 0 && j == 0) {
            return 1;
        }
        return arg.nextInt(10) == 0 ? 1 : BiomeLayers.OCEAN_ID;
    }
}

