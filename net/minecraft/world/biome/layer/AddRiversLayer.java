/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum AddRiversLayer implements MergingLayer,
IdentityCoordinateTransformer
{
    INSTANCE;

    private static final int FROZEN_RIVER_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int MUSHROOM_FIELDS_ID;
    private static final int MUSHROOM_FIELD_SHORE_ID;
    private static final int RIVER_ID;

    @Override
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int k = sampler1.sample(this.transformX(x), this.transformZ(z));
        int l = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (BiomeLayers.isOcean(k)) {
            return k;
        }
        if (l == RIVER_ID) {
            if (k == SNOWY_TUNDRA_ID) {
                return FROZEN_RIVER_ID;
            }
            if (k == MUSHROOM_FIELDS_ID || k == MUSHROOM_FIELD_SHORE_ID) {
                return MUSHROOM_FIELD_SHORE_ID;
            }
            return l & 0xFF;
        }
        return k;
    }

    static {
        FROZEN_RIVER_ID = BuiltinRegistries.BIOME.getRawId(Biomes.FROZEN_RIVER);
        SNOWY_TUNDRA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
        MUSHROOM_FIELDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
        MUSHROOM_FIELD_SHORE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELD_SHORE);
        RIVER_ID = BuiltinRegistries.BIOME.getRawId(Biomes.RIVER);
    }
}

