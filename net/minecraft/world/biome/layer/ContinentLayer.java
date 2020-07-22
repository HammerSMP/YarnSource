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
    public int sample(LayerRandomnessSource context, int x, int y) {
        if (x == 0 && y == 0) {
            return 1;
        }
        return context.nextInt(10) == 0 ? 1 : BiomeLayers.OCEAN_ID;
    }
}

