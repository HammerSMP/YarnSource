/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddMushroomIslandLayer implements DiagonalCrossSamplingLayer
{
    INSTANCE;

    private static final int MUSHROOM_FIELDS_ID;

    @Override
    public int sample(LayerRandomnessSource arg, int i, int j, int k, int l, int m) {
        if (BiomeLayers.isShallowOcean(m) && BiomeLayers.isShallowOcean(l) && BiomeLayers.isShallowOcean(i) && BiomeLayers.isShallowOcean(k) && BiomeLayers.isShallowOcean(j) && arg.nextInt(100) == 0) {
            return MUSHROOM_FIELDS_ID;
        }
        return m;
    }

    static {
        MUSHROOM_FIELDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
    }
}

