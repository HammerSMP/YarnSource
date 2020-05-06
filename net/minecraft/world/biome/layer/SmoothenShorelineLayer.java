/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum SmoothenShorelineLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource arg, int i, int j, int k, int l, int m) {
        boolean bl2;
        boolean bl = j == l;
        boolean bl3 = bl2 = i == k;
        if (bl == bl2) {
            if (bl) {
                return arg.nextInt(2) == 0 ? l : i;
            }
            return m;
        }
        return bl ? l : i;
    }
}

