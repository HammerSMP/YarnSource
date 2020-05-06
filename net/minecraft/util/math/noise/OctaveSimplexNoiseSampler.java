/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.ints.IntRBTreeSet
 *  it.unimi.dsi.fastutil.ints.IntSortedSet
 */
package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class OctaveSimplexNoiseSampler
implements NoiseSampler {
    private final SimplexNoiseSampler[] octaveSamplers;
    private final double field_20661;
    private final double field_20662;

    public OctaveSimplexNoiseSampler(ChunkRandom arg, IntStream intStream) {
        this(arg, (List)intStream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctaveSimplexNoiseSampler(ChunkRandom arg, List<Integer> list) {
        this(arg, (IntSortedSet)new IntRBTreeSet(list));
    }

    private OctaveSimplexNoiseSampler(ChunkRandom arg, IntSortedSet intSortedSet) {
        int j;
        if (intSortedSet.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -intSortedSet.firstInt();
        int k = i + (j = intSortedSet.lastInt()) + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        SimplexNoiseSampler lv = new SimplexNoiseSampler(arg);
        int l = j;
        this.octaveSamplers = new SimplexNoiseSampler[k];
        if (l >= 0 && l < k && intSortedSet.contains(0)) {
            this.octaveSamplers[l] = lv;
        }
        for (int m = l + 1; m < k; ++m) {
            if (m >= 0 && intSortedSet.contains(l - m)) {
                this.octaveSamplers[m] = new SimplexNoiseSampler(arg);
                continue;
            }
            arg.consume(262);
        }
        if (j > 0) {
            long n = (long)(lv.method_22416(lv.originX, lv.originY, lv.originZ) * 9.223372036854776E18);
            ChunkRandom lv2 = new ChunkRandom(n);
            for (int o = l - 1; o >= 0; --o) {
                if (o < k && intSortedSet.contains(l - o)) {
                    this.octaveSamplers[o] = new SimplexNoiseSampler(lv2);
                    continue;
                }
                lv2.consume(262);
            }
        }
        this.field_20662 = Math.pow(2.0, j);
        this.field_20661 = 1.0 / (Math.pow(2.0, k) - 1.0);
    }

    public double sample(double d, double e, boolean bl) {
        double f = 0.0;
        double g = this.field_20662;
        double h = this.field_20661;
        for (SimplexNoiseSampler lv : this.octaveSamplers) {
            if (lv != null) {
                f += lv.sample(d * g + (bl ? lv.originX : 0.0), e * g + (bl ? lv.originY : 0.0)) * h;
            }
            g /= 2.0;
            h *= 2.0;
        }
        return f;
    }

    @Override
    public double sample(double d, double e, double f, double g) {
        return this.sample(d, e, true) * 0.55;
    }
}

