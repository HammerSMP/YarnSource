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
        protected int sample(LayerSampleContext<?> context, int i, int j, int k, int l) {
            return context.choose(i, j, k, l);
        }
    };


    @Override
    public int transformX(int x) {
        return x >> 1;
    }

    @Override
    public int transformZ(int y) {
        return y >> 1;
    }

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int k = parent.sample(this.transformX(x), this.transformZ(z));
        context.initSeed(x >> 1 << 1, z >> 1 << 1);
        int l = x & 1;
        int m = z & 1;
        if (l == 0 && m == 0) {
            return k;
        }
        int n = parent.sample(this.transformX(x), this.transformZ(z + 1));
        int o = context.choose(k, n);
        if (l == 0 && m == 1) {
            return o;
        }
        int p = parent.sample(this.transformX(x + 1), this.transformZ(z));
        int q = context.choose(k, p);
        if (l == 1 && m == 0) {
            return q;
        }
        int r = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        return this.sample(context, k, p, n, r);
    }

    protected int sample(LayerSampleContext<?> context, int i, int j, int k, int l) {
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
        return context.choose(i, j, k, l);
    }
}

