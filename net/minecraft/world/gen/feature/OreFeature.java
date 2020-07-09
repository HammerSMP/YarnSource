/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class OreFeature
extends Feature<OreFeatureConfig> {
    public OreFeature(Codec<OreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, OreFeatureConfig arg4) {
        float f = random.nextFloat() * (float)Math.PI;
        float g = (float)arg4.SIZE / 8.0f;
        int i = MathHelper.ceil(((float)arg4.SIZE / 16.0f * 2.0f + 1.0f) / 2.0f);
        double d = (double)arg3.getX() + Math.sin(f) * (double)g;
        double e = (double)arg3.getX() - Math.sin(f) * (double)g;
        double h = (double)arg3.getZ() + Math.cos(f) * (double)g;
        double j = (double)arg3.getZ() - Math.cos(f) * (double)g;
        int k = 2;
        double l = arg3.getY() + random.nextInt(3) - 2;
        double m = arg3.getY() + random.nextInt(3) - 2;
        int n = arg3.getX() - MathHelper.ceil(g) - i;
        int o = arg3.getY() - 2 - i;
        int p = arg3.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);
        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o > arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) continue;
                return this.generateVeinPart(arg, random, arg4, d, e, h, j, l, m, n, o, p, q, r);
            }
        }
        return false;
    }

    protected boolean generateVeinPart(WorldAccess arg, Random random, OreFeatureConfig arg2, double d, double e, double f, double g, double h, double i, int j, int k, int l, int m, int n) {
        int o = 0;
        BitSet bitSet = new BitSet(m * n * m);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int p = arg2.SIZE;
        double[] ds = new double[p * 4];
        for (int q = 0; q < p; ++q) {
            float r = (float)q / (float)p;
            double s = MathHelper.lerp((double)r, d, e);
            double t = MathHelper.lerp((double)r, h, i);
            double u = MathHelper.lerp((double)r, f, g);
            double v = random.nextDouble() * (double)p / 16.0;
            double w = ((double)(MathHelper.sin((float)Math.PI * r) + 1.0f) * v + 1.0) / 2.0;
            ds[q * 4 + 0] = s;
            ds[q * 4 + 1] = t;
            ds[q * 4 + 2] = u;
            ds[q * 4 + 3] = w;
        }
        for (int x = 0; x < p - 1; ++x) {
            if (ds[x * 4 + 3] <= 0.0) continue;
            for (int y = x + 1; y < p; ++y) {
                double ab;
                double aa;
                double z;
                double ac;
                if (ds[y * 4 + 3] <= 0.0 || !((ac = ds[x * 4 + 3] - ds[y * 4 + 3]) * ac > (z = ds[x * 4 + 0] - ds[y * 4 + 0]) * z + (aa = ds[x * 4 + 1] - ds[y * 4 + 1]) * aa + (ab = ds[x * 4 + 2] - ds[y * 4 + 2]) * ab)) continue;
                if (ac > 0.0) {
                    ds[y * 4 + 3] = -1.0;
                    continue;
                }
                ds[x * 4 + 3] = -1.0;
            }
        }
        for (int ad = 0; ad < p; ++ad) {
            double ae = ds[ad * 4 + 3];
            if (ae < 0.0) continue;
            double af = ds[ad * 4 + 0];
            double ag = ds[ad * 4 + 1];
            double ah = ds[ad * 4 + 2];
            int ai = Math.max(MathHelper.floor(af - ae), j);
            int aj = Math.max(MathHelper.floor(ag - ae), k);
            int ak = Math.max(MathHelper.floor(ah - ae), l);
            int al = Math.max(MathHelper.floor(af + ae), ai);
            int am = Math.max(MathHelper.floor(ag + ae), aj);
            int an = Math.max(MathHelper.floor(ah + ae), ak);
            for (int ao = ai; ao <= al; ++ao) {
                double ap = ((double)ao + 0.5 - af) / ae;
                if (!(ap * ap < 1.0)) continue;
                for (int aq = aj; aq <= am; ++aq) {
                    double ar = ((double)aq + 0.5 - ag) / ae;
                    if (!(ap * ap + ar * ar < 1.0)) continue;
                    for (int as = ak; as <= an; ++as) {
                        int au;
                        double at = ((double)as + 0.5 - ah) / ae;
                        if (!(ap * ap + ar * ar + at * at < 1.0) || bitSet.get(au = ao - j + (aq - k) * m + (as - l) * m * n)) continue;
                        bitSet.set(au);
                        lv.set(ao, aq, as);
                        if (!arg2.RULE_TEST.test(arg.getBlockState(lv), random)) continue;
                        arg.setBlockState(lv, arg2.DEFAULT_STATE, 2);
                        ++o;
                    }
                }
            }
        }
        return o > 0;
    }
}

