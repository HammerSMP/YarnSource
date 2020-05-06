/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface ParentedLayer
extends CoordinateTransformer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> arg, LayerFactory<R> arg2) {
        return () -> {
            Object lv = arg2.make();
            return arg.createSampler((i, j) -> {
                arg.initSeed(i, j);
                return this.sample(arg, (LayerSampler)lv, i, j);
            }, lv);
        };
    }

    public int sample(LayerSampleContext<?> var1, LayerSampler var2, int var3, int var4);
}

