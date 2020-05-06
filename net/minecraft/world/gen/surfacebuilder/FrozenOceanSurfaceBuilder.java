/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class FrozenOceanSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
    protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState ICE = Blocks.ICE.getDefaultState();
    private OctaveSimplexNoiseSampler field_15644;
    private OctaveSimplexNoiseSampler field_15642;
    private long seed;

    public FrozenOceanSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        double e = 0.0;
        double f = 0.0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        float g = arg2.getTemperature(lv.set(i, 63, j));
        double h = Math.min(Math.abs(d), this.field_15644.sample((double)i * 0.1, (double)j * 0.1, false) * 15.0);
        if (h > 1.8) {
            double n = 0.09765625;
            e = h * h * 1.2;
            double o = Math.abs(this.field_15642.sample((double)i * 0.09765625, (double)j * 0.09765625, false));
            double p = Math.ceil(o * 40.0) + 14.0;
            if (e > p) {
                e = p;
            }
            if (g > 0.1f) {
                e -= 2.0;
            }
            if (e > 2.0) {
                f = (double)l - e - 7.0;
                e += (double)l;
            } else {
                e = 0.0;
            }
        }
        int q = i & 0xF;
        int r = j & 0xF;
        BlockState lv2 = arg2.getSurfaceConfig().getUnderMaterial();
        BlockState lv3 = arg2.getSurfaceConfig().getTopMaterial();
        int s = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int t = -1;
        int u = 0;
        int v = 2 + random.nextInt(4);
        int w = l + 18 + random.nextInt(10);
        for (int x = Math.max(k, (int)e + 1); x >= 0; --x) {
            lv.set(q, x, r);
            if (arg.getBlockState(lv).isAir() && x < (int)e && random.nextDouble() > 0.01) {
                arg.setBlockState(lv, PACKED_ICE, false);
            } else if (arg.getBlockState(lv).getMaterial() == Material.WATER && x > (int)f && x < l && f != 0.0 && random.nextDouble() > 0.15) {
                arg.setBlockState(lv, PACKED_ICE, false);
            }
            BlockState lv4 = arg.getBlockState(lv);
            if (lv4.isAir()) {
                t = -1;
                continue;
            }
            if (lv4.isOf(arg3.getBlock())) {
                if (t == -1) {
                    if (s <= 0) {
                        lv3 = AIR;
                        lv2 = arg3;
                    } else if (x >= l - 4 && x <= l + 1) {
                        lv3 = arg2.getSurfaceConfig().getTopMaterial();
                        lv2 = arg2.getSurfaceConfig().getUnderMaterial();
                    }
                    if (x < l && (lv3 == null || lv3.isAir())) {
                        lv3 = arg2.getTemperature(lv.set(i, x, j)) < 0.15f ? ICE : arg4;
                    }
                    t = s;
                    if (x >= l - 1) {
                        arg.setBlockState(lv, lv3, false);
                        continue;
                    }
                    if (x < l - 7 - s) {
                        lv3 = AIR;
                        lv2 = arg3;
                        arg.setBlockState(lv, GRAVEL, false);
                        continue;
                    }
                    arg.setBlockState(lv, lv2, false);
                    continue;
                }
                if (t <= 0) continue;
                arg.setBlockState(lv, lv2, false);
                if (--t != 0 || !lv2.isOf(Blocks.SAND) || s <= 1) continue;
                t = random.nextInt(4) + Math.max(0, x - 63);
                lv2 = lv2.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
                continue;
            }
            if (!lv4.isOf(Blocks.PACKED_ICE) || u > v || x <= w) continue;
            arg.setBlockState(lv, SNOW_BLOCK, false);
            ++u;
        }
    }

    @Override
    public void initSeed(long l) {
        if (this.seed != l || this.field_15644 == null || this.field_15642 == null) {
            ChunkRandom lv = new ChunkRandom(l);
            this.field_15644 = new OctaveSimplexNoiseSampler(lv, IntStream.rangeClosed(-3, 0));
            this.field_15642 = new OctaveSimplexNoiseSampler(lv, (List<Integer>)ImmutableList.of((Object)0));
        }
        this.seed = l;
    }
}

