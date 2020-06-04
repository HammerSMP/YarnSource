/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import java.util.Random;

public class IntRange {
    private final int min;
    private final int max;

    public IntRange(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + i + ", Given max: " + j);
        }
        this.min = i;
        this.max = j;
    }

    public static IntRange between(int i, int j) {
        return new IntRange(i, j);
    }

    public int choose(Random random) {
        if (this.min == this.max) {
            return this.min;
        }
        return random.nextInt(this.max - this.min + 1) + this.min;
    }

    public int method_29492() {
        return this.min;
    }

    public int method_29493() {
        return this.max;
    }

    public String toString() {
        return "IntRange[" + this.min + "-" + this.max + "]";
    }
}

