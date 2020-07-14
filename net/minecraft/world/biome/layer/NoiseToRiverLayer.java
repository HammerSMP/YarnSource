/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum NoiseToRiverLayer implements CrossSamplingLayer
{
    INSTANCE;

    public static final int RIVER_ID;

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int n2 = NoiseToRiverLayer.isValidForRiver(center);
        if (n2 == NoiseToRiverLayer.isValidForRiver(w) && n2 == NoiseToRiverLayer.isValidForRiver(n) && n2 == NoiseToRiverLayer.isValidForRiver(e) && n2 == NoiseToRiverLayer.isValidForRiver(s)) {
            return -1;
        }
        return RIVER_ID;
    }

    private static int isValidForRiver(int value) {
        if (value >= 2) {
            return 2 + (value & 1);
        }
        return value;
    }

    static {
        RIVER_ID = BuiltinRegistries.BIOME.getRawId(Biomes.RIVER);
    }
}

