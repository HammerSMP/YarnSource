/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public interface CrossSamplingLayer
extends ParentedLayer,
NorthWestCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2, int var3, int var4, int var5, int var6);

    @Override
    default public int sample(LayerSampleContext<?> arg, LayerSampler arg2, int i, int j) {
        return this.sample(arg, arg2.sample(this.transformX(i + 1), this.transformZ(j + 0)), arg2.sample(this.transformX(i + 2), this.transformZ(j + 1)), arg2.sample(this.transformX(i + 1), this.transformZ(j + 2)), arg2.sample(this.transformX(i + 0), this.transformZ(j + 1)), arg2.sample(this.transformX(i + 1), this.transformZ(j + 1)));
    }
}

