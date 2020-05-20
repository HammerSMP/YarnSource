/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class DoublePerlinNoiseSampler {
    private final double amplitude;
    private final OctavePerlinNoiseSampler firstSampler;
    private final OctavePerlinNoiseSampler secondSampler;

    public DoublePerlinNoiseSampler(ChunkRandom arg, IntStream intStream) {
        this(arg, (List)intStream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public DoublePerlinNoiseSampler(ChunkRandom arg, List<Integer> list) {
        this.firstSampler = new OctavePerlinNoiseSampler(arg, list);
        this.secondSampler = new OctavePerlinNoiseSampler(arg, list);
        int i = list.stream().min(Integer::compareTo).orElse(0);
        int j = list.stream().max(Integer::compareTo).orElse(0);
        this.amplitude = 0.16666666666666666 / DoublePerlinNoiseSampler.createAmplitude(j - i);
    }

    private static double createAmplitude(int i) {
        return 0.1 * (1.0 + 1.0 / (double)(i + 1));
    }

    public double sample(double d, double e, double f) {
        double g = d * 1.0181268882175227;
        double h = e * 1.0181268882175227;
        double i = f * 1.0181268882175227;
        return (this.firstSampler.sample(d, e, f) + this.secondSampler.sample(g, h, i)) * this.amplitude;
    }
}

