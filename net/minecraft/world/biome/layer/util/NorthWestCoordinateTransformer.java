/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer.util;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;

public interface NorthWestCoordinateTransformer
extends CoordinateTransformer {
    @Override
    default public int transformX(int i) {
        return i - 1;
    }

    @Override
    default public int transformZ(int i) {
        return i - 1;
    }
}

