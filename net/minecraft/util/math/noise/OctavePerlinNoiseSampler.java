/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.ints.IntRBTreeSet
 *  it.unimi.dsi.fastutil.ints.IntSortedSet
 *  javax.annotation.Nullable
 */
package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class OctavePerlinNoiseSampler
implements NoiseSampler {
    private final PerlinNoiseSampler[] octaveSamplers;
    private final double field_20659;
    private final double field_20660;

    public OctavePerlinNoiseSampler(ChunkRandom arg, IntStream intStream) {
        this(arg, (List)intStream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctavePerlinNoiseSampler(ChunkRandom arg, List<Integer> list) {
        this(arg, (IntSortedSet)new IntRBTreeSet(list));
    }

    private OctavePerlinNoiseSampler(ChunkRandom arg, IntSortedSet intSortedSet) {
        int j;
        if (intSortedSet.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -intSortedSet.firstInt();
        int k = i + (j = intSortedSet.lastInt()) + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        PerlinNoiseSampler lv = new PerlinNoiseSampler(arg);
        int l = j;
        this.octaveSamplers = new PerlinNoiseSampler[k];
        if (l >= 0 && l < k && intSortedSet.contains(0)) {
            this.octaveSamplers[l] = lv;
        }
        for (int m = l + 1; m < k; ++m) {
            if (m >= 0 && intSortedSet.contains(l - m)) {
                this.octaveSamplers[m] = new PerlinNoiseSampler(arg);
                continue;
            }
            arg.consume(262);
        }
        if (j > 0) {
            long n = (long)(lv.sample(0.0, 0.0, 0.0, 0.0, 0.0) * 9.223372036854776E18);
            ChunkRandom lv2 = new ChunkRandom(n);
            for (int o = l - 1; o >= 0; --o) {
                if (o < k && intSortedSet.contains(l - o)) {
                    this.octaveSamplers[o] = new PerlinNoiseSampler(lv2);
                    continue;
                }
                lv2.consume(262);
            }
        }
        this.field_20660 = Math.pow(2.0, j);
        this.field_20659 = 1.0 / (Math.pow(2.0, k) - 1.0);
    }

    public double sample(double d, double e, double f) {
        return this.sample(d, e, f, 0.0, 0.0, false);
    }

    public double sample(double d, double e, double f, double g, double h, boolean bl) {
        double i = 0.0;
        double j = this.field_20660;
        double k = this.field_20659;
        for (PerlinNoiseSampler lv : this.octaveSamplers) {
            if (lv != null) {
                i += lv.sample(OctavePerlinNoiseSampler.maintainPrecision(d * j), bl ? -lv.originY : OctavePerlinNoiseSampler.maintainPrecision(e * j), OctavePerlinNoiseSampler.maintainPrecision(f * j), g * j, h * j) * k;
            }
            j /= 2.0;
            k *= 2.0;
        }
        return i;
    }

    @Nullable
    public PerlinNoiseSampler getOctave(int i) {
        return this.octaveSamplers[i];
    }

    public static double maintainPrecision(double d) {
        return d - (double)MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
    }

    @Override
    public double sample(double d, double e, double f, double g) {
        return this.sample(d, e, 0.0, f, g, false);
    }
}

