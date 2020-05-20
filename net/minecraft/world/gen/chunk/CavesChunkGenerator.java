/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.gen.chunk;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class CavesChunkGenerator
extends SurfaceChunkGenerator<CavesChunkGeneratorConfig> {
    private final double[] noiseFalloff = this.buildNoiseFalloff();
    private final CavesChunkGeneratorConfig generatorConfig;

    public CavesChunkGenerator(BiomeSource arg, long l, CavesChunkGeneratorConfig arg2) {
        super(arg, l, arg2, 4, 8, 128, false);
        this.generatorConfig = arg2;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator create(long l) {
        return new CavesChunkGenerator(this.biomeSource.create(l), l, this.generatorConfig);
    }

    @Override
    protected void sampleNoiseColumn(double[] ds, int i, int j) {
        double d = 684.412;
        double e = 2053.236;
        double f = 8.555150000000001;
        double g = 34.2206;
        int k = -10;
        int l = 3;
        this.sampleNoiseColumn(ds, i, j, 684.412, 2053.236, 8.555150000000001, 34.2206, 3, -10);
    }

    @Override
    protected double[] computeNoiseRange(int i, int j) {
        return new double[]{0.0, 0.0};
    }

    @Override
    protected double computeNoiseFalloff(double d, double e, int i) {
        return this.noiseFalloff[i];
    }

    private double[] buildNoiseFalloff() {
        double[] ds = new double[this.getNoiseSizeY()];
        for (int i = 0; i < this.getNoiseSizeY(); ++i) {
            ds[i] = Math.cos((double)i * Math.PI * 6.0 / (double)this.getNoiseSizeY()) * 2.0;
            double d = i;
            if (i > this.getNoiseSizeY() / 2) {
                d = this.getNoiseSizeY() - 1 - i;
            }
            if (!(d < 4.0)) continue;
            d = 4.0 - d;
            int n = i;
            ds[n] = ds[n] - d * d * d * 10.0;
        }
        return ds;
    }

    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(Biome arg, StructureAccessor arg2, SpawnGroup arg3, BlockPos arg4) {
        if (arg3 == SpawnGroup.MONSTER && Feature.NETHER_BRIDGE.isInsideStructure(arg2, arg4)) {
            return Feature.NETHER_BRIDGE.getMonsterSpawns();
        }
        return super.getEntitySpawnList(arg, arg2, arg3, arg4);
    }

    @Override
    public int getMaxY() {
        return 128;
    }

    @Override
    public int getSeaLevel() {
        return 32;
    }

    @Override
    public int getHeight(int i, int j, Heightmap.Type arg) {
        return this.getMaxY() / 2;
    }
}

