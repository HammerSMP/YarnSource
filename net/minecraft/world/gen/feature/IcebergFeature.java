/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

public class IcebergFeature
extends Feature<SingleStateFeatureConfig> {
    public IcebergFeature(Function<Dynamic<?>, ? extends SingleStateFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, SingleStateFeatureConfig arg5) {
        boolean bl3;
        int l;
        arg4 = new BlockPos(arg4.getX(), arg.getSeaLevel(), arg4.getZ());
        boolean bl = random.nextDouble() > 0.7;
        BlockState lv = arg5.state;
        double d = random.nextDouble() * 2.0 * Math.PI;
        int i = 11 - random.nextInt(5);
        int j = 3 + random.nextInt(3);
        boolean bl2 = random.nextDouble() > 0.7;
        int k = 11;
        int n = l = bl2 ? random.nextInt(6) + 6 : random.nextInt(15) + 3;
        if (!bl2 && random.nextDouble() > 0.9) {
            l += random.nextInt(19) + 7;
        }
        int m = Math.min(l + random.nextInt(11), 18);
        int n2 = Math.min(l + random.nextInt(7) - random.nextInt(5), 11);
        int o = bl2 ? i : 11;
        for (int p = -o; p < o; ++p) {
            for (int q = -o; q < o; ++q) {
                for (int r = 0; r < l; ++r) {
                    int s;
                    int n3 = s = bl2 ? this.method_13417(r, l, n2) : this.method_13419(random, r, l, n2);
                    if (!bl2 && p >= s) continue;
                    this.method_13426(arg, random, arg4, l, p, r, q, s, o, bl2, j, d, bl, lv);
                }
            }
        }
        this.method_13418(arg, arg4, n2, l, bl2, i);
        for (int t = -o; t < o; ++t) {
            for (int u = -o; u < o; ++u) {
                for (int v = -1; v > -m; --v) {
                    int w = bl2 ? MathHelper.ceil((float)o * (1.0f - (float)Math.pow(v, 2.0) / ((float)m * 8.0f))) : o;
                    int x = this.method_13427(random, -v, m, n2);
                    if (t >= x) continue;
                    this.method_13426(arg, random, arg4, m, t, v, u, x, w, bl2, j, d, bl, lv);
                }
            }
        }
        boolean bl4 = bl2 ? random.nextDouble() > 0.1 : (bl3 = random.nextDouble() > 0.7);
        if (bl3) {
            this.method_13428(random, arg, n2, l, arg4, bl2, i, d, j);
        }
        return true;
    }

    private void method_13428(Random random, WorldAccess arg, int i, int j, BlockPos arg2, boolean bl, int k, double d, int l) {
        int m = random.nextBoolean() ? -1 : 1;
        int n = random.nextBoolean() ? -1 : 1;
        int o = random.nextInt(Math.max(i / 2 - 2, 1));
        if (random.nextBoolean()) {
            o = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
        }
        int p = random.nextInt(Math.max(i / 2 - 2, 1));
        if (random.nextBoolean()) {
            p = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
        }
        if (bl) {
            o = p = random.nextInt(Math.max(k - 5, 1));
        }
        BlockPos lv = new BlockPos(m * o, 0, n * p);
        double e = bl ? d + 1.5707963267948966 : random.nextDouble() * 2.0 * Math.PI;
        for (int q = 0; q < j - 3; ++q) {
            int r = this.method_13419(random, q, j, i);
            this.method_13415(r, q, arg2, arg, false, e, lv, k, l);
        }
        for (int s = -1; s > -j + random.nextInt(5); --s) {
            int t = this.method_13427(random, -s, j, i);
            this.method_13415(t, s, arg2, arg, true, e, lv, k, l);
        }
    }

    private void method_13415(int i, int j, BlockPos arg, WorldAccess arg2, boolean bl, double d, BlockPos arg3, int k, int l) {
        int m = i + 1 + k / 3;
        int n = Math.min(i - 3, 3) + l / 2 - 1;
        for (int o = -m; o < m; ++o) {
            for (int p = -m; p < m; ++p) {
                BlockPos lv;
                Block lv2;
                double e = this.method_13424(o, p, arg3, m, n, d);
                if (!(e < 0.0) || !this.isSnowyOrIcy(lv2 = arg2.getBlockState(lv = arg.add(o, j, p)).getBlock()) && lv2 != Blocks.SNOW_BLOCK) continue;
                if (bl) {
                    this.setBlockState(arg2, lv, Blocks.WATER.getDefaultState());
                    continue;
                }
                this.setBlockState(arg2, lv, Blocks.AIR.getDefaultState());
                this.clearSnowAbove(arg2, lv);
            }
        }
    }

    private void clearSnowAbove(WorldAccess arg, BlockPos arg2) {
        if (arg.getBlockState(arg2.up()).isOf(Blocks.SNOW)) {
            this.setBlockState(arg, arg2.up(), Blocks.AIR.getDefaultState());
        }
    }

    private void method_13426(WorldAccess arg, Random random, BlockPos arg2, int i, int j, int k, int l, int m, int n, boolean bl, int o, double d, boolean bl2, BlockState arg3) {
        double e;
        double d2 = e = bl ? this.method_13424(j, l, BlockPos.ORIGIN, n, this.method_13416(k, i, o), d) : this.method_13421(j, l, BlockPos.ORIGIN, m, random);
        if (e < 0.0) {
            double f;
            BlockPos lv = arg2.add(j, k, l);
            double d3 = f = bl ? -0.5 : (double)(-6 - random.nextInt(3));
            if (e > f && random.nextDouble() > 0.9) {
                return;
            }
            this.method_13425(lv, arg, random, i - k, i, bl, bl2, arg3);
        }
    }

    private void method_13425(BlockPos arg, WorldAccess arg2, Random random, int i, int j, boolean bl, boolean bl2, BlockState arg3) {
        BlockState lv = arg2.getBlockState(arg);
        if (lv.getMaterial() == Material.AIR || lv.isOf(Blocks.SNOW_BLOCK) || lv.isOf(Blocks.ICE) || lv.isOf(Blocks.WATER)) {
            int k;
            boolean bl3 = !bl || random.nextDouble() > 0.05;
            int n = k = bl ? 3 : 2;
            if (bl2 && !lv.isOf(Blocks.WATER) && (double)i <= (double)random.nextInt(Math.max(1, j / k)) + (double)j * 0.6 && bl3) {
                this.setBlockState(arg2, arg, Blocks.SNOW_BLOCK.getDefaultState());
            } else {
                this.setBlockState(arg2, arg, arg3);
            }
        }
    }

    private int method_13416(int i, int j, int k) {
        int l = k;
        if (i > 0 && j - i <= 3) {
            l -= 4 - (j - i);
        }
        return l;
    }

    private double method_13421(int i, int j, BlockPos arg, int k, Random random) {
        float f = 10.0f * MathHelper.clamp(random.nextFloat(), 0.2f, 0.8f) / (float)k;
        return (double)f + Math.pow(i - arg.getX(), 2.0) + Math.pow(j - arg.getZ(), 2.0) - Math.pow(k, 2.0);
    }

    private double method_13424(int i, int j, BlockPos arg, int k, int l, double d) {
        return Math.pow(((double)(i - arg.getX()) * Math.cos(d) - (double)(j - arg.getZ()) * Math.sin(d)) / (double)k, 2.0) + Math.pow(((double)(i - arg.getX()) * Math.sin(d) + (double)(j - arg.getZ()) * Math.cos(d)) / (double)l, 2.0) - 1.0;
    }

    private int method_13419(Random random, int i, int j, int k) {
        float f = 3.5f - random.nextFloat();
        float g = (1.0f - (float)Math.pow(i, 2.0) / ((float)j * f)) * (float)k;
        if (j > 15 + random.nextInt(5)) {
            int l = i < 3 + random.nextInt(6) ? i / 2 : i;
            g = (1.0f - (float)l / ((float)j * f * 0.4f)) * (float)k;
        }
        return MathHelper.ceil(g / 2.0f);
    }

    private int method_13417(int i, int j, int k) {
        float f = 1.0f;
        float g = (1.0f - (float)Math.pow(i, 2.0) / ((float)j * 1.0f)) * (float)k;
        return MathHelper.ceil(g / 2.0f);
    }

    private int method_13427(Random random, int i, int j, int k) {
        float f = 1.0f + random.nextFloat() / 2.0f;
        float g = (1.0f - (float)i / ((float)j * f)) * (float)k;
        return MathHelper.ceil(g / 2.0f);
    }

    private boolean isSnowyOrIcy(Block arg) {
        return arg == Blocks.PACKED_ICE || arg == Blocks.SNOW_BLOCK || arg == Blocks.BLUE_ICE;
    }

    private boolean isAirBelow(BlockView arg, BlockPos arg2) {
        return arg.getBlockState(arg2.down()).getMaterial() == Material.AIR;
    }

    private void method_13418(WorldAccess arg, BlockPos arg2, int i, int j, boolean bl, int k) {
        int l = bl ? k : i / 2;
        for (int m = -l; m <= l; ++m) {
            for (int n = -l; n <= l; ++n) {
                for (int o = 0; o <= j; ++o) {
                    BlockPos lv = arg2.add(m, o, n);
                    Block lv2 = arg.getBlockState(lv).getBlock();
                    if (!this.isSnowyOrIcy(lv2) && lv2 != Blocks.SNOW) continue;
                    if (this.isAirBelow(arg, lv)) {
                        this.setBlockState(arg, lv, Blocks.AIR.getDefaultState());
                        this.setBlockState(arg, lv.up(), Blocks.AIR.getDefaultState());
                        continue;
                    }
                    if (!this.isSnowyOrIcy(lv2)) continue;
                    Block[] lvs = new Block[]{arg.getBlockState(lv.west()).getBlock(), arg.getBlockState(lv.east()).getBlock(), arg.getBlockState(lv.north()).getBlock(), arg.getBlockState(lv.south()).getBlock()};
                    int p = 0;
                    for (Block lv3 : lvs) {
                        if (this.isSnowyOrIcy(lv3)) continue;
                        ++p;
                    }
                    if (p < 3) continue;
                    this.setBlockState(arg, lv, Blocks.AIR.getDefaultState());
                }
            }
        }
    }
}

