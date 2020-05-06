/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai;

import net.minecraft.util.math.IntRange;

public class Durations {
    public static IntRange betweenSeconds(int i, int j) {
        return new IntRange(i * 20, j * 20);
    }
}

