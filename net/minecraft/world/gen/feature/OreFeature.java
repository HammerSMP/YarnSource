/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class OreFeature
extends Feature<OreFeatureConfig> {
    public OreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, OreFeatureConfig arg5) {
        float f = random.nextFloat() * (float)Math.PI;
        float g = (float)arg5.size / 8.0f;
        int i = MathHelper.ceil(((float)arg5.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        double d = (float)arg4.getX() + MathHelper.sin(f) * g;
        double e = (float)arg4.getX() - MathHelper.sin(f) * g;
        double h = (float)arg4.getZ() + MathHelper.cos(f) * g;
        double j = (float)arg4.getZ() - MathHelper.cos(f) * g;
        int k = 2;
        double l = arg4.getY() + random.nextInt(3) - 2;
        double m = arg4.getY() + random.nextInt(3) - 2;
        int n = arg4.getX() - MathHelper.ceil(g) - i;
        int o = arg4.getY() - 2 - i;
        int p = arg4.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);
        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o > arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) continue;
                return this.generateVeinPart(arg, random, arg5, d, e, h, j, l, m, n, o, p, q, r);
            }
        }
        return false;
    }

    protected boolean generateVeinPart(IWorld arg, Random random, OreFeatureConfig arg2, double d, double e, double f, double g, double h, double i, int j, int k, int l, int m, int n) {
        int o = 0;
        BitSet bitSet = new BitSet(m * n * m);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        double[] ds = new double[arg2.size * 4];
        for (int p = 0; p < arg2.size; ++p) {
            float q = (float)p / (float)arg2.size;
            double r = MathHelper.lerp((double)q, d, e);
            double s = MathHelper.lerp((double)q, h, i);
            double t = MathHelper.lerp((double)q, f, g);
            double u = random.nextDouble() * (double)arg2.size / 16.0;
            double v = ((double)(MathHelper.sin((float)Math.PI * q) + 1.0f) * u + 1.0) / 2.0;
            ds[p * 4 + 0] = r;
            ds[p * 4 + 1] = s;
            ds[p * 4 + 2] = t;
            ds[p * 4 + 3] = v;
        }
        for (int w = 0; w < arg2.size - 1; ++w) {
            if (ds[w * 4 + 3] <= 0.0) continue;
            for (int x = w + 1; x < arg2.size; ++x) {
                double aa;
                double z;
                double y;
                double ab;
                if (ds[x * 4 + 3] <= 0.0 || !((ab = ds[w * 4 + 3] - ds[x * 4 + 3]) * ab > (y = ds[w * 4 + 0] - ds[x * 4 + 0]) * y + (z = ds[w * 4 + 1] - ds[x * 4 + 1]) * z + (aa = ds[w * 4 + 2] - ds[x * 4 + 2]) * aa)) continue;
                if (ab > 0.0) {
                    ds[x * 4 + 3] = -1.0;
                    continue;
                }
                ds[w * 4 + 3] = -1.0;
            }
        }
        for (int ac = 0; ac < arg2.size; ++ac) {
            double ad = ds[ac * 4 + 3];
            if (ad < 0.0) continue;
            double ae = ds[ac * 4 + 0];
            double af = ds[ac * 4 + 1];
            double ag = ds[ac * 4 + 2];
            int ah = Math.max(MathHelper.floor(ae - ad), j);
            int ai = Math.max(MathHelper.floor(af - ad), k);
            int aj = Math.max(MathHelper.floor(ag - ad), l);
            int ak = Math.max(MathHelper.floor(ae + ad), ah);
            int al = Math.max(MathHelper.floor(af + ad), ai);
            int am = Math.max(MathHelper.floor(ag + ad), aj);
            for (int an = ah; an <= ak; ++an) {
                double ao = ((double)an + 0.5 - ae) / ad;
                if (!(ao * ao < 1.0)) continue;
                for (int ap = ai; ap <= al; ++ap) {
                    double aq = ((double)ap + 0.5 - af) / ad;
                    if (!(ao * ao + aq * aq < 1.0)) continue;
                    for (int ar = aj; ar <= am; ++ar) {
                        int at;
                        double as = ((double)ar + 0.5 - ag) / ad;
                        if (!(ao * ao + aq * aq + as * as < 1.0) || bitSet.get(at = an - j + (ap - k) * m + (ar - l) * m * n)) continue;
                        bitSet.set(at);
                        lv.set(an, ap, ar);
                        if (!arg2.target.getCondition().test(arg.getBlockState(lv))) continue;
                        arg.setBlockState(lv, arg2.state, 2);
                        ++o;
                    }
                }
            }
        }
        return o > 0;
    }
}

