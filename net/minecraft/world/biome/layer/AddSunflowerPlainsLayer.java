/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.class_5458;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddSunflowerPlainsLayer implements SouthEastSamplingLayer
{
    INSTANCE;

    private static final int PLAINS_ID;
    private static final int SUNFLOWER_PLAINS;

    @Override
    public int sample(LayerRandomnessSource arg, int i) {
        if (arg.nextInt(57) == 0 && i == PLAINS_ID) {
            return SUNFLOWER_PLAINS;
        }
        return i;
    }

    static {
        PLAINS_ID = class_5458.field_25933.getRawId(Biomes.PLAINS);
        SUNFLOWER_PLAINS = class_5458.field_25933.getRawId(Biomes.SUNFLOWER_PLAINS);
    }
}

