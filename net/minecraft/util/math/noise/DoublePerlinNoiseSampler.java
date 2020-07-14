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

    public DoublePerlinNoiseSampler(ChunkRandom arg, List<Integer> octaves) {
        this.firstSampler = new OctavePerlinNoiseSampler(arg, octaves);
        this.secondSampler = new OctavePerlinNoiseSampler(arg, octaves);
        int i = octaves.stream().min(Integer::compareTo).orElse(0);
        int j = octaves.stream().max(Integer::compareTo).orElse(0);
        this.amplitude = 0.16666666666666666 / DoublePerlinNoiseSampler.createAmplitude(j - i);
    }

    private static double createAmplitude(int octaves) {
        return 0.1 * (1.0 + 1.0 / (double)(octaves + 1));
    }

    public double sample(double x, double y, double z) {
        double g = x * 1.0181268882175227;
        double h = y * 1.0181268882175227;
        double i = z * 1.0181268882175227;
        return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(g, h, i)) * this.amplitude;
    }
}

