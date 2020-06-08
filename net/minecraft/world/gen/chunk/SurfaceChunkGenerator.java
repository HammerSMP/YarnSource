/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
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
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.NoiseConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.StructureFeature;

public final class SurfaceChunkGenerator
extends ChunkGenerator {
    public static final Codec<SurfaceChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BiomeSource.field_24713.fieldOf("biome_source").forGetter(arg -> arg.biomeSource), (App)Codec.LONG.fieldOf("seed").stable().forGetter(arg -> arg.field_24778), (App)ChunkGeneratorType.field_24781.fieldOf("settings").forGetter(arg -> arg.field_24774)).apply((Applicative)instance, instance.stable((Object)((Function3)SurfaceChunkGenerator::new))));
    private static final float[] field_16649 = Util.make(new float[13824], fs -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    fs[i * 24 * 24 + j * 24 + k] = (float)SurfaceChunkGenerator.method_16571(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private static final float[] field_24775 = Util.make(new float[25], fs -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f;
                fs[i + 2 + (j + 2) * 5] = f = 10.0f / MathHelper.sqrt((float)(i * i + j * j) + 0.2f);
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
    private final OctavePerlinNoiseSampler field_24776;
    @Nullable
    private final SimplexNoiseSampler field_24777;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final long field_24778;
    protected final ChunkGeneratorType field_24774;
    private final int field_24779;

    public SurfaceChunkGenerator(BiomeSource arg, long l, ChunkGeneratorType arg2) {
        this(arg, arg, l, arg2);
    }

    private SurfaceChunkGenerator(BiomeSource arg, BiomeSource arg2, long l, ChunkGeneratorType arg3) {
        super(arg, arg2, arg3.getConfig(), l);
        this.field_24778 = l;
        this.field_24774 = arg3;
        NoiseConfig lv = arg3.method_28559();
        this.field_24779 = lv.getHeight();
        this.verticalNoiseResolution = lv.getSizeVertical() * 4;
        this.horizontalNoiseResolution = lv.getSizeHorizontal() * 4;
        this.defaultBlock = arg3.getDefaultBlock();
        this.defaultFluid = arg3.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = lv.getHeight() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(l);
        this.lowerInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.upperInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.interpolationNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceDepthNoise = lv.hasSimplexSurfaceNoise() ? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0));
        this.random.consume(2620);
        this.field_24776 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        if (lv.hasIslandNoiseOverride()) {
            ChunkRandom lv2 = new ChunkRandom(l);
            lv2.consume(17292);
            this.field_24777 = new SimplexNoiseSampler(lv2);
        } else {
            this.field_24777 = null;
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> method_28506() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator withSeed(long l) {
        return new SurfaceChunkGenerator(this.biomeSource.withSeed(l), l, this.field_24774);
    }

    public boolean method_28548(long l, ChunkGeneratorType.Preset arg) {
        return this.field_24778 == l && this.field_24774.method_28555(arg);
    }

    private double sampleNoise(int i, int j, int k, double d, double e, double f, double g) {
        double h = 0.0;
        double l = 0.0;
        double m = 0.0;
        boolean bl = true;
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

    private double[] sampleNoiseColumn(int i, int j) {
        double[] ds = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(ds, i, j);
        return ds;
    }

    private void sampleNoiseColumn(double[] ds, int i, int j) {
        double ad;
        double ac;
        NoiseConfig lv = this.field_24774.method_28559();
        if (this.field_24777 != null) {
            double d = TheEndBiomeSource.getNoiseAt(this.field_24777, i, j) - 8.0f;
            if (d > 0.0) {
                double e = 0.25;
            } else {
                double f = 1.0;
            }
        } else {
            float g = 0.0f;
            float h = 0.0f;
            float k = 0.0f;
            int l = 2;
            int m = this.getSeaLevel();
            float n = this.biomeSource.getBiomeForNoiseGen(i, m, j).getDepth();
            for (int o = -2; o <= 2; ++o) {
                for (int p = -2; p <= 2; ++p) {
                    float v;
                    float u;
                    Biome lv2 = this.biomeSource.getBiomeForNoiseGen(i + o, m, j + p);
                    float q = lv2.getDepth();
                    float r = lv2.getScale();
                    if (lv.isAmplified() && q > 0.0f) {
                        float s = 1.0f + q * 2.0f;
                        float t = 1.0f + r * 4.0f;
                    } else {
                        u = q;
                        v = r;
                    }
                    float w = q > n ? 0.5f : 1.0f;
                    float x = w * field_24775[o + 2 + (p + 2) * 5] / (u + 2.0f);
                    g += v * x;
                    h += u * x;
                    k += x;
                }
            }
            float y = h / k;
            float z = g / k;
            double aa = y * 0.5f - 0.125f;
            double ab = z * 0.9f + 0.1f;
            ac = aa * 0.265625;
            ad = 96.0 / ab;
        }
        double ae = 684.412 * lv.getSampling().getXZScale();
        double af = 684.412 * lv.getSampling().getYScale();
        double ag = ae / lv.getSampling().getXZFactor();
        double ah = af / lv.getSampling().getYFactor();
        double ai = lv.getTopSlide().getTarget();
        double aj = lv.getTopSlide().getSize();
        double ak = lv.getTopSlide().getOffset();
        double al = lv.getBottomSlide().getTarget();
        double am = lv.getBottomSlide().getSize();
        double an = lv.getBottomSlide().getOffset();
        double ao = lv.hasRandomDensityOffset() ? this.method_28553(i, j) : 0.0;
        double ap = lv.getDensityFactor();
        double aq = lv.getDensityOffset();
        for (int ar = 0; ar <= this.noiseSizeY; ++ar) {
            double as = this.sampleNoise(i, ar, j, ae, af, ag, ah);
            double at = 1.0 - (double)ar * 2.0 / (double)this.noiseSizeY + ao;
            double au = at * ap + aq;
            double av = (au + ac) * ad;
            as = av > 0.0 ? (as += av * 4.0) : (as += av);
            if (aj > 0.0) {
                double aw = ((double)(this.noiseSizeY - ar) - ak) / aj;
                as = MathHelper.clampedLerp(ai, as, aw);
            }
            if (am > 0.0) {
                double ax = ((double)ar - an) / am;
                as = MathHelper.clampedLerp(al, as, ax);
            }
            ds[ar] = as;
        }
    }

    private double method_28553(int i, int j) {
        double f;
        double d = this.field_24776.sample(i * 200, 10.0, j * 200, 1.0, 0.0, true);
        if (d < 0.0) {
            double e = -d * 0.3;
        } else {
            f = d;
        }
        double g = f * 24.575625 - 2.0;
        if (g < 0.0) {
            return g * 0.009486607142857142;
        }
        return Math.min(g, 1.0) * 0.006640625;
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

    private void buildBedrock(Chunk arg, Random random) {
        boolean bl2;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int i = arg.getPos().getStartX();
        int j = arg.getPos().getStartZ();
        int k = this.field_24774.getBedrockFloorY();
        int l = this.field_24779 - 1 - this.field_24774.getBedrockCeilingY();
        int m = 5;
        boolean bl = l + 4 >= 0 && l < this.field_24779;
        boolean bl3 = bl2 = k + 4 >= 0 && k < this.field_24779;
        if (!bl && !bl2) {
            return;
        }
        for (BlockPos lv2 : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
            if (bl) {
                for (int n = 0; n < 5; ++n) {
                    if (n > random.nextInt(5)) continue;
                    arg.setBlockState(lv.set(lv2.getX(), l - n, lv2.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            if (!bl2) continue;
            for (int o = 4; o >= 0; --o) {
                if (o > random.nextInt(5)) continue;
                arg.setBlockState(lv.set(lv2.getX(), k + o, lv2.getZ()), Blocks.BEDROCK.getDefaultState(), false);
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
        for (StructureFeature<?> lv2 : StructureFeature.field_24861) {
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

    @Override
    public int getMaxY() {
        return this.field_24779;
    }

    @Override
    public int getSeaLevel() {
        return this.field_24774.method_28561();
    }

    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(Biome arg, StructureAccessor arg2, SpawnGroup arg3, BlockPos arg4) {
        if (arg2.method_28388(arg4, true, StructureFeature.SWAMP_HUT).hasChildren()) {
            if (arg3 == SpawnGroup.MONSTER) {
                return StructureFeature.SWAMP_HUT.getMonsterSpawns();
            }
            if (arg3 == SpawnGroup.CREATURE) {
                return StructureFeature.SWAMP_HUT.getCreatureSpawns();
            }
        }
        if (arg3 == SpawnGroup.MONSTER) {
            if (arg2.method_28388(arg4, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
                return StructureFeature.PILLAGER_OUTPOST.getMonsterSpawns();
            }
            if (arg2.method_28388(arg4, false, StructureFeature.MONUMENT).hasChildren()) {
                return StructureFeature.MONUMENT.getMonsterSpawns();
            }
            if (arg2.method_28388(arg4, true, StructureFeature.FORTRESS).hasChildren()) {
                return StructureFeature.FORTRESS.getMonsterSpawns();
            }
        }
        return super.getEntitySpawnList(arg, arg2, arg3, arg4);
    }

    @Override
    public void populateEntities(ChunkRegion arg) {
        if (this.field_24774.method_28562()) {
            return;
        }
        int i = arg.getCenterChunkX();
        int j = arg.getCenterChunkZ();
        Biome lv = arg.getBiome(new ChunkPos(i, j).getCenterBlockPos());
        ChunkRandom lv2 = new ChunkRandom();
        lv2.setPopulationSeed(arg.getSeed(), i << 4, j << 4);
        SpawnHelper.populateEntities(arg, lv, i, j, lv2);
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

