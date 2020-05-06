/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ScaleLayer implements ParentedLayer
{
    NORMAL,
    FUZZY{

        @Override
        protected int sample(LayerSampleContext<?> arg, int i, int j, int k, int l) {
            return arg.choose(i, j, k, l);
        }
    };


    @Override
    public int transformX(int i) {
        return i >> 1;
    }

    @Override
    public int transformZ(int i) {
        return i >> 1;
    }

    @Override
    public int sample(LayerSampleContext<?> arg, LayerSampler arg2, int i, int j) {
        int k = arg2.sample(this.transformX(i), this.transformZ(j));
        arg.initSeed(i >> 1 << 1, j >> 1 << 1);
        int l = i & 1;
        int m = j & 1;
        if (l == 0 && m == 0) {
            return k;
        }
        int n = arg2.sample(this.transformX(i), this.transformZ(j + 1));
        int o = arg.choose(k, n);
        if (l == 0 && m == 1) {
            return o;
        }
        int p = arg2.sample(this.transformX(i + 1), this.transformZ(j));
        int q = arg.choose(k, p);
        if (l == 1 && m == 0) {
            return q;
        }
        int r = arg2.sample(this.transformX(i + 1), this.transformZ(j + 1));
        return this.sample(arg, k, p, n, r);
    }

    protected int sample(LayerSampleContext<?> arg, int i, int j, int k, int l) {
        if (j == k && k == l) {
            return j;
        }
        if (i == j && i == k) {
            return i;
        }
        if (i == j && i == l) {
            return i;
        }
        if (i == k && i == l) {
            return i;
        }
        if (i == j && k != l) {
            return i;
        }
        if (i == k && j != l) {
            return i;
        }
        if (i == l && j != k) {
            return i;
        }
        if (j == k && i != l) {
            return j;
        }
        if (j == l && i != k) {
            return j;
        }
        if (k == l && i != j) {
            return k;
        }
        return arg.choose(i, j, k, l);
    }
}

