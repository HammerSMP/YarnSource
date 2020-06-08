/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.util.math;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class WordPackedArray {
    private final long[] array;
    private final int unitSize;
    private final long maxValue;
    private final int length;

    public WordPackedArray(int i, int j) {
        this(i, j, new long[MathHelper.roundUpToMultiple(j * i, 64) / 64]);
    }

    public WordPackedArray(int i, int j, long[] ls) {
        Validate.inclusiveBetween((long)1L, (long)32L, (long)i);
        this.length = j;
        this.unitSize = i;
        this.array = ls;
        this.maxValue = (1L << i) - 1L;
        int k = MathHelper.roundUpToMultiple(j * i, 64) / 64;
        if (ls.length != k) {
            throw new IllegalArgumentException("Invalid length given for storage, got: " + ls.length + " but expected: " + k);
        }
    }

    public void set(int i, int j) {
        Validate.inclusiveBetween((long)0L, (long)(this.length - 1), (long)i);
        Validate.inclusiveBetween((long)0L, (long)this.maxValue, (long)j);
        int k = i * this.unitSize;
        int l = k >> 6;
        int m = (i + 1) * this.unitSize - 1 >> 6;
        int n = k ^ l << 6;
        this.array[l] = this.array[l] & (this.maxValue << n ^ 0xFFFFFFFFFFFFFFFFL) | ((long)j & this.maxValue) << n;
        if (l != m) {
            int o = 64 - n;
            int p = this.unitSize - o;
            this.array[m] = this.array[m] >>> p << p | ((long)j & this.maxValue) >> o;
        }
    }

    public int get(int i) {
        Validate.inclusiveBetween((long)0L, (long)(this.length - 1), (long)i);
        int j = i * this.unitSize;
        int k = j >> 6;
        int l = (i + 1) * this.unitSize - 1 >> 6;
        int m = j ^ k << 6;
        if (k == l) {
            return (int)(this.array[k] >>> m & this.maxValue);
        }
        int n = 64 - m;
        return (int)((this.array[k] >>> m | this.array[l] << n) & this.maxValue);
    }

    public long[] getAlignedArray() {
        return this.array;
    }

    public int getUnitSize() {
        return this.unitSize;
    }
}

