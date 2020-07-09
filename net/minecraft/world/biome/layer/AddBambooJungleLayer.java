/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.class_5458;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddBambooJungleLayer implements SouthEastSamplingLayer
{
    INSTANCE;

    private static final int JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_ID;

    @Override
    public int sample(LayerRandomnessSource arg, int i) {
        if (arg.nextInt(10) == 0 && i == JUNGLE_ID) {
            return BAMBOO_JUNGLE_ID;
        }
        return i;
    }

    static {
        JUNGLE_ID = class_5458.field_25933.getRawId(Biomes.JUNGLE);
        BAMBOO_JUNGLE_ID = class_5458.field_25933.getRawId(Biomes.BAMBOO_JUNGLE);
    }
}

