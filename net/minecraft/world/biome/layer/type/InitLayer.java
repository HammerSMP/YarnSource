/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface InitLayer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> arg) {
        return () -> arg.createSampler((i, j) -> {
            arg.initSeed(i, j);
            return this.sample(arg, i, j);
        });
    }

    public int sample(LayerRandomnessSource var1, int var2, int var3);
}

