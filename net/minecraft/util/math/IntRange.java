/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import java.util.Random;

public class IntRange {
    private final int min;
    private final int max;

    public IntRange(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + min + ", Given max: " + max);
        }
        this.min = min;
        this.max = max;
    }

    public static IntRange between(int min, int max) {
        return new IntRange(min, max);
    }

    public int choose(Random random) {
        if (this.min == this.max) {
            return this.min;
        }
        return random.nextInt(this.max - this.min + 1) + this.min;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public String toString() {
        return "IntRange[" + this.min + "-" + this.max + "]";
    }
}

