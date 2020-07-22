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
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        boolean bl2;
        boolean bl = e == w;
        boolean bl3 = bl2 = n == s;
        if (bl == bl2) {
            if (bl) {
                return context.nextInt(2) == 0 ? w : n;
            }
            return center;
        }
        return bl ? w : n;
    }
}

