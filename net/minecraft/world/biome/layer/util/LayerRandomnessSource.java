/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.util;

import net.minecraft.util.math.noise.PerlinNoiseSampler;

public interface LayerRandomnessSource {
    public int nextInt(int var1);

    public PerlinNoiseSampler getNoiseSampler();
}

