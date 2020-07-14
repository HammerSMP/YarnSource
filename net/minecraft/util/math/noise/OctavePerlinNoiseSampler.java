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

    public OctavePerlinNoiseSampler(ChunkRandom random, IntStream octaves) {
        this(random, (List)octaves.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctavePerlinNoiseSampler(ChunkRandom random, List<Integer> octaves) {
        this(random, (IntSortedSet)new IntRBTreeSet(octaves));
    }

    private OctavePerlinNoiseSampler(ChunkRandom random, IntSortedSet octaves) {
        int j;
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -octaves.firstInt();
        int k = i + (j = octaves.lastInt()) + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        PerlinNoiseSampler lv = new PerlinNoiseSampler(random);
        int l = j;
        this.octaveSamplers = new PerlinNoiseSampler[k];
        if (l >= 0 && l < k && octaves.contains(0)) {
            this.octaveSamplers[l] = lv;
        }
        for (int m = l + 1; m < k; ++m) {
            if (m >= 0 && octaves.contains(l - m)) {
                this.octaveSamplers[m] = new PerlinNoiseSampler(random);
                continue;
            }
            random.consume(262);
        }
        if (j > 0) {
            long n = (long)(lv.sample(0.0, 0.0, 0.0, 0.0, 0.0) * 9.223372036854776E18);
            ChunkRandom lv2 = new ChunkRandom(n);
            for (int o = l - 1; o >= 0; --o) {
                if (o < k && octaves.contains(l - o)) {
                    this.octaveSamplers[o] = new PerlinNoiseSampler(lv2);
                    continue;
                }
                lv2.consume(262);
            }
        }
        this.field_20660 = Math.pow(2.0, j);
        this.field_20659 = 1.0 / (Math.pow(2.0, k) - 1.0);
    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0, 0.0, false);
    }

    public double sample(double x, double y, double z, double g, double h, boolean bl) {
        double i = 0.0;
        double j = this.field_20660;
        double k = this.field_20659;
        for (PerlinNoiseSampler lv : this.octaveSamplers) {
            if (lv != null) {
                i += lv.sample(OctavePerlinNoiseSampler.maintainPrecision(x * j), bl ? -lv.originY : OctavePerlinNoiseSampler.maintainPrecision(y * j), OctavePerlinNoiseSampler.maintainPrecision(z * j), g * j, h * j) * k;
            }
            j /= 2.0;
            k *= 2.0;
        }
        return i;
    }

    @Nullable
    public PerlinNoiseSampler getOctave(int octave) {
        return this.octaveSamplers[octave];
    }

    public static double maintainPrecision(double d) {
        return d - (double)MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
    }

    @Override
    public double sample(double x, double y, double f, double g) {
        return this.sample(x, y, 0.0, f, g, false);
    }
}

