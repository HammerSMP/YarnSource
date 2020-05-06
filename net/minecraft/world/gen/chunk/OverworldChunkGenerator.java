/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.level.LevelGeneratorType;

public class OverworldChunkGenerator
extends SurfaceChunkGenerator<OverworldChunkGeneratorConfig> {
    private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], fs -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f;
                fs[i + 2 + (j + 2) * 5] = f = 10.0f / MathHelper.sqrt((float)(i * i + j * j) + 0.2f);
            }
        }
    });
    private final OctavePerlinNoiseSampler noiseSampler;
    private final boolean amplified;
    private final PhantomSpawner phantomSpawner = new PhantomSpawner();
    private final PillagerSpawner pillagerSpawner = new PillagerSpawner();
    private final CatSpawner catSpawner = new CatSpawner();
    private final ZombieSiegeManager zombieSiegeManager = new ZombieSiegeManager();

    public OverworldChunkGenerator(IWorld arg, BiomeSource arg2, OverworldChunkGeneratorConfig arg3) {
        super(arg, arg2, 4, 8, 256, arg3, true);
        this.random.consume(2620);
        this.noiseSampler = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.amplified = arg.getLevelProperties().getGeneratorType() == LevelGeneratorType.AMPLIFIED;
    }

    @Override
    public void populateEntities(ChunkRegion arg) {
        int i = arg.getCenterChunkX();
        int j = arg.getCenterChunkZ();
        Biome lv = arg.getBiome(new ChunkPos(i, j).getCenterBlockPos());
        ChunkRandom lv2 = new ChunkRandom();
        lv2.setPopulationSeed(arg.getSeed(), i << 4, j << 4);
        SpawnHelper.populateEntities(arg, lv, i, j, lv2);
    }

    @Override
    protected void sampleNoiseColumn(double[] ds, int i, int j) {
        double d = 684.412f;
        double e = 684.412f;
        double f = 8.555149841308594;
        double g = 4.277574920654297;
        int k = -10;
        int l = 3;
        this.sampleNoiseColumn(ds, i, j, 684.412f, 684.412f, 8.555149841308594, 4.277574920654297, 3, -10);
    }

    @Override
    protected double computeNoiseFalloff(double d, double e, int i) {
        double f = 8.5;
        double g = ((double)i - (8.5 + d * 8.5 / 8.0 * 4.0)) * 12.0 * 128.0 / 256.0 / e;
        if (g < 0.0) {
            g *= 4.0;
        }
        return g;
    }

    @Override
    protected double[] computeNoiseRange(int i, int j) {
        double[] ds = new double[2];
        float f = 0.0f;
        float g = 0.0f;
        float h = 0.0f;
        int k = 2;
        int l = this.getSeaLevel();
        float m = this.biomeSource.getBiomeForNoiseGen(i, l, j).getDepth();
        for (int n = -2; n <= 2; ++n) {
            for (int o = -2; o <= 2; ++o) {
                Biome lv = this.biomeSource.getBiomeForNoiseGen(i + n, l, j + o);
                float p = lv.getDepth();
                float q = lv.getScale();
                if (this.amplified && p > 0.0f) {
                    p = 1.0f + p * 2.0f;
                    q = 1.0f + q * 4.0f;
                }
                float r = BIOME_WEIGHT_TABLE[n + 2 + (o + 2) * 5] / (p + 2.0f);
                if (lv.getDepth() > m) {
                    r /= 2.0f;
                }
                f += q * r;
                g += p * r;
                h += r;
            }
        }
        f /= h;
        g /= h;
        f = f * 0.9f + 0.1f;
        g = (g * 4.0f - 1.0f) / 8.0f;
        ds[0] = (double)g + this.sampleNoise(i, j);
        ds[1] = f;
        return ds;
    }

    private double sampleNoise(int i, int j) {
        double d = this.noiseSampler.sample(i * 200, 10.0, j * 200, 1.0, 0.0, true) * 65535.0 / 8000.0;
        if (d < 0.0) {
            d = -d * 0.3;
        }
        if ((d = d * 3.0 - 2.0) < 0.0) {
            d /= 28.0;
        } else {
            if (d > 1.0) {
                d = 1.0;
            }
            d /= 40.0;
        }
        return d;
    }

    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(StructureAccessor arg, EntityCategory arg2, BlockPos arg3) {
        if (Feature.SWAMP_HUT.method_14029(this.world, arg, arg3)) {
            if (arg2 == EntityCategory.MONSTER) {
                return Feature.SWAMP_HUT.getMonsterSpawns();
            }
            if (arg2 == EntityCategory.CREATURE) {
                return Feature.SWAMP_HUT.getCreatureSpawns();
            }
        } else if (arg2 == EntityCategory.MONSTER) {
            if (Feature.PILLAGER_OUTPOST.isApproximatelyInsideStructure(this.world, arg, arg3)) {
                return Feature.PILLAGER_OUTPOST.getMonsterSpawns();
            }
            if (Feature.OCEAN_MONUMENT.isApproximatelyInsideStructure(this.world, arg, arg3)) {
                return Feature.OCEAN_MONUMENT.getMonsterSpawns();
            }
        }
        return super.getEntitySpawnList(arg, arg2, arg3);
    }

    @Override
    public void spawnEntities(ServerWorld arg, boolean bl, boolean bl2) {
        this.phantomSpawner.spawn(arg, bl, bl2);
        this.pillagerSpawner.spawn(arg, bl, bl2);
        this.catSpawner.spawn(arg, bl, bl2);
        this.zombieSiegeManager.spawn(arg, bl, bl2);
    }

    @Override
    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }
}

