/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface MergingLayer
extends CoordinateTransformer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> arg, LayerFactory<R> arg2, LayerFactory<R> arg3) {
        return () -> {
            Object lv = arg2.make();
            Object lv2 = arg3.make();
            return arg.createSampler((i, j) -> {
                arg.initSeed(i, j);
                return this.sample(arg, (LayerSampler)lv, (LayerSampler)lv2, i, j);
            }, lv, lv2);
        };
    }

    public int sample(LayerRandomnessSource var1, LayerSampler var2, LayerSampler var3, int var4, int var5);
}

