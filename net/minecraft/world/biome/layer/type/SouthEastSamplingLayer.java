/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public interface SouthEastSamplingLayer
extends ParentedLayer,
NorthWestCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2);

    @Override
    default public int sample(LayerSampleContext<?> arg, LayerSampler arg2, int i, int j) {
        int k = arg2.sample(this.transformX(i + 1), this.transformZ(j + 1));
        return this.sample(arg, k);
    }
}

