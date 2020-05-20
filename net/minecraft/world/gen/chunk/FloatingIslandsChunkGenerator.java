/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.gen.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5284;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public class FloatingIslandsChunkGenerator
extends SurfaceChunkGenerator<class_5284> {
    private final class_5284 generatorConfig;

    public FloatingIslandsChunkGenerator(BiomeSource arg, long l, class_5284 arg2) {
        super(arg, l, arg2, 8, 4, 128, true);
        this.generatorConfig = arg2;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator create(long l) {
        return new FloatingIslandsChunkGenerator(this.biomeSource.create(l), l, this.generatorConfig);
    }

    @Override
    protected void sampleNoiseColumn(double[] ds, int i, int j) {
        double d = 1368.824;
        double e = 684.412;
        double f = 17.110300000000002;
        double g = 4.277575000000001;
        int k = 64;
        int l = -3000;
        this.sampleNoiseColumn(ds, i, j, 1368.824, 684.412, 17.110300000000002, 4.277575000000001, 64, -3000);
    }

    @Override
    protected double[] computeNoiseRange(int i, int j) {
        return new double[]{this.biomeSource.getNoiseAt(i, j), 0.0};
    }

    @Override
    protected double computeNoiseFalloff(double d, double e, int i) {
        return 8.0 - d;
    }

    @Override
    protected double topInterpolationStart() {
        return (int)super.topInterpolationStart() / 2;
    }

    @Override
    protected double bottomInterpolationStart() {
        return 8.0;
    }

    @Override
    public int getSpawnHeight() {
        return 50;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }
}

