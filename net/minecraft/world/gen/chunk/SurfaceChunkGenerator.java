/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5284;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class SurfaceChunkGenerator<T extends class_5284>
extends ChunkGenerator {
    private static final float[] field_16649 = Util.make(new float[13824], fs -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    fs[i * 24 * 24 + j * 24 + k] = (float)SurfaceChunkGenerator.method_16571(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    protected final ChunkRandom random;
    private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
    private final OctavePerlinNoiseSampler upperInterpolatedNoise;
    private final OctavePerlinNoiseSampler interpolationNoise;
    private final NoiseSampler surfaceDepthNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final int bedrockFloorHeight;
    private final int bedrockCeilingHeight;

    public SurfaceChunkGenerator(BiomeSource arg, long l, T arg2, int i, int j, int k, boolean bl) {
        super(arg, ((class_5284)arg2).getConfig());
        this.verticalNoiseResolution = j;
        this.horizontalNoiseResolution = i;
        this.defaultBlock = ((class_5284)arg2).getDefaultBlock();
        this.defaultFluid = ((class_5284)arg2).getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = k / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(l);
        this.lowerInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.upperInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.interpolationNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceDepthNoise = bl ? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0));
        this.bedrockFloorHeight = ((class_5284)arg2).getBedrockFloorY();
        this.bedrockCeilingHeight = ((class_5284)arg2).getBedrockCeilingY();
    }

    private double sampleNoise(int i, int j, int k, double d, double e, double f, double g) {
        double h = 0.0;
        double l = 0.0;
        double m = 0.0;
        double n = 1.0;
        for (int o = 0; o < 16; ++o) {
            PerlinNoiseSampler lv3;
            PerlinNoiseSampler lv2;
            double p = OctavePerlinNoiseSampler.maintainPrecision((double)i * d * n);
            double q = OctavePerlinNoiseSampler.maintainPrecision((double)j * e * n);
            double r = OctavePerlinNoiseSampler.maintainPrecision((double)k * d * n);
            double s = e * n;
            PerlinNoiseSampler lv = this.lowerInterpolatedNoise.getOctave(o);
            if (lv != null) {
                h += lv.sample(p, q, r, s, (double)j * s) / n;
            }
            if ((lv2 = this.upperInterpolatedNoise.getOctave(o)) != null) {
                l += lv2.sample(p, q, r, s, (double)j * s) / n;
            }
            if (o < 8 && (lv3 = this.interpolationNoise.getOctave(o)) != null) {
                m += lv3.sample(OctavePerlinNoiseSampler.maintainPrecision((double)i * f * n), OctavePerlinNoiseSampler.maintainPrecision((double)j * g * n), OctavePerlinNoiseSampler.maintainPrecision((double)k * f * n), g * n, (double)j * g * n) / n;
            }
            n /= 2.0;
        }
        return MathHelper.clampedLerp(h / 512.0, l / 512.0, (m / 10.0 + 1.0) / 2.0);
    }

    protected double[] sampleNoiseColumn(int i, int j) {
        double[] ds = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(ds, i, j);
        return ds;
    }

    protected void sampleNoiseColumn(double[] ds, int i, int j, double d, double e, double f, double g, int k, int l) {
        double[] es = this.computeNoiseRange(i, j);
        double h = es[0];
        double m = es[1];
        double n = this.topInterpolationStart();
        double o = this.bottomInterpolationStart();
        for (int p = 0; p < this.getNoiseSizeY(); ++p) {
            double q = this.sampleNoise(i, p, j, d, e, f, g);
            q -= this.computeNoiseFalloff(h, m, p);
            if ((double)p > n) {
                q = MathHelper.clampedLerp(q, l, ((double)p - n) / (double)k);
            } else if ((double)p < o) {
                q = MathHelper.clampedLerp(q, -30.0, (o - (double)p) / (o - 1.0));
            }
            ds[p] = q;
        }
    }

    protected abstract double[] computeNoiseRange(int var1, int var2);

    protected abstract double computeNoiseFalloff(double var1, double var3, int var5);

    protected double topInterpolationStart() {
        return this.getNoiseSizeY() - 4;
    }

    protected double bottomInterpolationStart() {
        return 0.0;
    }

    @Override
    public int getHeight(int i, int j, Heightmap.Type arg) {
        return this.sampleHeightmap(i, j, null, arg.getBlockPredicate());
    }

    @Override
    public BlockView getColumnSample(int i, int j) {
        BlockState[] lvs = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
        this.sampleHeightmap(i, j, lvs, null);
        return new VerticalBlockSample(lvs);
    }

    private int sampleHeightmap(int i, int j, @Nullable BlockState[] args, @Nullable Predicate<BlockState> predicate) {
        int k = Math.floorDiv(i, this.horizontalNoiseResolution);
        int l = Math.floorDiv(j, this.horizontalNoiseResolution);
        int m = Math.floorMod(i, this.horizontalNoiseResolution);
        int n = Math.floorMod(j, this.horizontalNoiseResolution);
        double d = (double)m / (double)this.horizontalNoiseResolution;
        double e = (double)n / (double)this.horizontalNoiseResolution;
        double[][] ds = new double[][]{this.sampleNoiseColumn(k, l), this.sampleNoiseColumn(k, l + 1), this.sampleNoiseColumn(k + 1, l), this.sampleNoiseColumn(k + 1, l + 1)};
        for (int o = this.noiseSizeY - 1; o >= 0; --o) {
            double f = ds[0][o];
            double g = ds[1][o];
            double h = ds[2][o];
            double p = ds[3][o];
            double q = ds[0][o + 1];
            double r = ds[1][o + 1];
            double s = ds[2][o + 1];
            double t = ds[3][o + 1];
            for (int u = this.verticalNoiseResolution - 1; u >= 0; --u) {
                double v = (double)u / (double)this.verticalNoiseResolution;
                double w = MathHelper.lerp3(v, d, e, f, q, h, s, g, r, p, t);
                int x = o * this.verticalNoiseResolution + u;
                BlockState lv = this.getBlockState(w, x);
                if (args != null) {
                    args[x] = lv;
                }
                if (predicate == null || !predicate.test(lv)) continue;
                return x + 1;
            }
        }
        return 0;
    }

    protected BlockState getBlockState(double d, int i) {
        BlockState lv3;
        if (d > 0.0) {
            BlockState lv = this.defaultBlock;
        } else if (i < this.getSeaLevel()) {
            BlockState lv2 = this.defaultFluid;
        } else {
            lv3 = AIR;
        }
        return lv3;
    }

    protected abstract void sampleNoiseColumn(double[] var1, int var2, int var3);

    public int getNoiseSizeY() {
        return this.noiseSizeY + 1;
    }

    @Override
    public void buildSurface(ChunkRegion arg, Chunk arg2) {
        ChunkPos lv = arg2.getPos();
        int i = lv.x;
        int j = lv.z;
        ChunkRandom lv2 = new ChunkRandom();
        lv2.setTerrainSeed(i, j);
        ChunkPos lv3 = arg2.getPos();
        int k = lv3.getStartX();
        int l = lv3.getStartZ();
        double d = 0.0625;
        BlockPos.Mutable lv4 = new BlockPos.Mutable();
        for (int m = 0; m < 16; ++m) {
            for (int n = 0; n < 16; ++n) {
                int o = k + m;
                int p = l + n;
                int q = arg2.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n) + 1;
                double e = this.surfaceDepthNoise.sample((double)o * 0.0625, (double)p * 0.0625, 0.0625, (double)m * 0.0625) * 15.0;
                arg.getBiome(lv4.set(k + m, q, l + n)).buildSurface(lv2, arg2, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), arg.getSeed());
            }
        }
        this.buildBedrock(arg2, lv2);
    }

    protected void buildBedrock(Chunk arg, Random random) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int i = arg.getPos().getStartX();
        int j = arg.getPos().getStartZ();
        int k = this.bedrockFloorHeight;
        int l = this.bedrockCeilingHeight;
        for (BlockPos lv2 : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
            if (l > 0) {
                for (int m = l; m >= l - 4; --m) {
                    if (m < l - random.nextInt(5)) continue;
                    arg.setBlockState(lv.set(lv2.getX(), m, lv2.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            if (k >= 256) continue;
            for (int n = k + 4; n >= k; --n) {
                if (n > k + random.nextInt(5)) continue;
                arg.setBlockState(lv.set(lv2.getX(), n, lv2.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
        }
    }

    @Override
    public void populateNoise(WorldAccess arg, StructureAccessor arg2, Chunk arg3) {
        ObjectArrayList objectList = new ObjectArrayList(10);
        ObjectArrayList objectList2 = new ObjectArrayList(32);
        ChunkPos lv = arg3.getPos();
        int i = lv.x;
        int j = lv.z;
        int k = i << 4;
        int l = j << 4;
        for (StructureFeature<?> lv2 : Feature.JIGSAW_STRUCTURES) {
            arg2.getStructuresWithChildren(ChunkSectionPos.from(lv, 0), lv2).forEach(arg_0 -> SurfaceChunkGenerator.method_26983(lv, (ObjectList)objectList, k, l, (ObjectList)objectList2, arg_0));
        }
        double[][][] ds = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];
        for (int m = 0; m < this.noiseSizeZ + 1; ++m) {
            ds[0][m] = new double[this.noiseSizeY + 1];
            this.sampleNoiseColumn(ds[0][m], i * this.noiseSizeX, j * this.noiseSizeZ + m);
            ds[1][m] = new double[this.noiseSizeY + 1];
        }
        ProtoChunk lv3 = (ProtoChunk)arg3;
        Heightmap lv4 = lv3.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap lv5 = lv3.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable lv6 = new BlockPos.Mutable();
        ObjectListIterator objectListIterator = objectList.iterator();
        ObjectListIterator objectListIterator2 = objectList2.iterator();
        for (int n = 0; n < this.noiseSizeX; ++n) {
            for (int o = 0; o < this.noiseSizeZ + 1; ++o) {
                this.sampleNoiseColumn(ds[1][o], i * this.noiseSizeX + n + 1, j * this.noiseSizeZ + o);
            }
            for (int p = 0; p < this.noiseSizeZ; ++p) {
                ChunkSection lv7 = lv3.getSection(15);
                lv7.lock();
                for (int q = this.noiseSizeY - 1; q >= 0; --q) {
                    double d = ds[0][p][q];
                    double e = ds[0][p + 1][q];
                    double f = ds[1][p][q];
                    double g = ds[1][p + 1][q];
                    double h = ds[0][p][q + 1];
                    double r = ds[0][p + 1][q + 1];
                    double s = ds[1][p][q + 1];
                    double t = ds[1][p + 1][q + 1];
                    for (int u = this.verticalNoiseResolution - 1; u >= 0; --u) {
                        int v = q * this.verticalNoiseResolution + u;
                        int w = v & 0xF;
                        int x = v >> 4;
                        if (lv7.getYOffset() >> 4 != x) {
                            lv7.unlock();
                            lv7 = lv3.getSection(x);
                            lv7.lock();
                        }
                        double y = (double)u / (double)this.verticalNoiseResolution;
                        double z = MathHelper.lerp(y, d, h);
                        double aa = MathHelper.lerp(y, f, s);
                        double ab = MathHelper.lerp(y, e, r);
                        double ac = MathHelper.lerp(y, g, t);
                        for (int ad = 0; ad < this.horizontalNoiseResolution; ++ad) {
                            int ae = k + n * this.horizontalNoiseResolution + ad;
                            int af = ae & 0xF;
                            double ag = (double)ad / (double)this.horizontalNoiseResolution;
                            double ah = MathHelper.lerp(ag, z, aa);
                            double ai = MathHelper.lerp(ag, ab, ac);
                            for (int aj = 0; aj < this.horizontalNoiseResolution; ++aj) {
                                int ak = l + p * this.horizontalNoiseResolution + aj;
                                int al = ak & 0xF;
                                double am = (double)aj / (double)this.horizontalNoiseResolution;
                                double an = MathHelper.lerp(am, ah, ai);
                                double ao = MathHelper.clamp(an / 200.0, -1.0, 1.0);
                                ao = ao / 2.0 - ao * ao * ao / 24.0;
                                while (objectListIterator.hasNext()) {
                                    StructurePiece lv8 = (StructurePiece)objectListIterator.next();
                                    BlockBox lv9 = lv8.getBoundingBox();
                                    int ap = Math.max(0, Math.max(lv9.minX - ae, ae - lv9.maxX));
                                    int aq = v - (lv9.minY + (lv8 instanceof PoolStructurePiece ? ((PoolStructurePiece)lv8).getGroundLevelDelta() : 0));
                                    int ar = Math.max(0, Math.max(lv9.minZ - ak, ak - lv9.maxZ));
                                    ao += SurfaceChunkGenerator.method_16572(ap, aq, ar) * 0.8;
                                }
                                objectListIterator.back(objectList.size());
                                while (objectListIterator2.hasNext()) {
                                    JigsawJunction lv10 = (JigsawJunction)objectListIterator2.next();
                                    int as = ae - lv10.getSourceX();
                                    int at = v - lv10.getSourceGroundY();
                                    int au = ak - lv10.getSourceZ();
                                    ao += SurfaceChunkGenerator.method_16572(as, at, au) * 0.4;
                                }
                                objectListIterator2.back(objectList2.size());
                                BlockState lv11 = this.getBlockState(ao, v);
                                if (lv11 == AIR) continue;
                                if (lv11.getLuminance() != 0) {
                                    lv6.set(ae, v, ak);
                                    lv3.addLightSource(lv6);
                                }
                                lv7.setBlockState(af, w, al, lv11, false);
                                lv4.trackUpdate(af, v, al, lv11);
                                lv5.trackUpdate(af, v, al, lv11);
                            }
                        }
                    }
                }
                lv7.unlock();
            }
            double[][] es = ds[0];
            ds[0] = ds[1];
            ds[1] = es;
        }
    }

    private static double method_16572(int i, int j, int k) {
        int l = i + 12;
        int m = j + 12;
        int n = k + 12;
        if (l < 0 || l >= 24) {
            return 0.0;
        }
        if (m < 0 || m >= 24) {
            return 0.0;
        }
        if (n < 0 || n >= 24) {
            return 0.0;
        }
        return field_16649[n * 24 * 24 + l * 24 + m];
    }

    private static double method_16571(int i, int j, int k) {
        double d = i * i + k * k;
        double e = (double)j + 0.5;
        double f = e * e;
        double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
        double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
        return h * g;
    }

    private static /* synthetic */ void method_26983(ChunkPos arg, ObjectList objectList, int i, int j, ObjectList objectList2, StructureStart arg2) {
        for (StructurePiece lv : arg2.getChildren()) {
            if (!lv.intersectsChunk(arg, 12)) continue;
            if (lv instanceof PoolStructurePiece) {
                PoolStructurePiece lv2 = (PoolStructurePiece)lv;
                StructurePool.Projection lv3 = lv2.getPoolElement().getProjection();
                if (lv3 == StructurePool.Projection.RIGID) {
                    objectList.add((Object)lv2);
                }
                for (JigsawJunction lv4 : lv2.getJunctions()) {
                    int k = lv4.getSourceX();
                    int l = lv4.getSourceZ();
                    if (k <= i - 12 || l <= j - 12 || k >= i + 15 + 12 || l >= j + 15 + 12) continue;
                    objectList2.add((Object)lv4);
                }
                continue;
            }
            objectList.add((Object)lv);
        }
    }
}

