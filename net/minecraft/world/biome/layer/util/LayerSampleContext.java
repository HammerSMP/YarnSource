/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.util;

import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface LayerSampleContext<R extends LayerSampler>
extends LayerRandomnessSource {
    public void initSeed(long var1, long var3);

    public R createSampler(LayerOperator var1);

    default public R createSampler(LayerOperator operator, R parent) {
        return this.createSampler(operator);
    }

    default public R createSampler(LayerOperator operator, R arg2, R arg3) {
        return this.createSampler(operator);
    }

    default public int choose(int a, int b) {
        return this.nextInt(2) == 0 ? a : b;
    }

    default public int choose(int a, int b, int c, int d) {
        int m = this.nextInt(4);
        if (m == 0) {
            return a;
        }
        if (m == 1) {
            return b;
        }
        if (m == 2) {
            return c;
        }
        return d;
    }
}

