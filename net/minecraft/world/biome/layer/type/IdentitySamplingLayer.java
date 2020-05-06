/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface IdentitySamplingLayer
extends ParentedLayer,
IdentityCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2);

    @Override
    default public int sample(LayerSampleContext<?> arg, LayerSampler arg2, int i, int j) {
        return this.sample(arg, arg2.sample(this.transformX(i), this.transformZ(j)));
    }
}

