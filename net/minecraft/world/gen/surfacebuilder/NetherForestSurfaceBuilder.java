/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class NetherForestSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected long seed;
    private OctavePerlinNoiseSampler field_22203;

    public NetherForestSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        int n = l;
        int o = i & 0xF;
        int p = j & 0xF;
        double e = this.field_22203.sample((double)i * 0.1, l, (double)j * 0.1);
        boolean bl = e > 0.15 + random.nextDouble() * 0.35;
        double f = this.field_22203.sample((double)i * 0.1, 109.0, (double)j * 0.1);
        boolean bl2 = f > 0.25 + random.nextDouble() * 0.9;
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int r = -1;
        BlockState lv2 = arg5.getUnderMaterial();
        for (int s = 127; s >= 0; --s) {
            lv.set(o, s, p);
            BlockState lv3 = arg5.getTopMaterial();
            BlockState lv4 = arg.getBlockState(lv);
            if (lv4.isAir()) {
                r = -1;
                continue;
            }
            if (!lv4.isOf(arg3.getBlock())) continue;
            if (r == -1) {
                boolean bl3 = false;
                if (q <= 0) {
                    bl3 = true;
                    lv2 = arg5.getUnderMaterial();
                }
                if (bl) {
                    lv3 = arg5.getUnderMaterial();
                } else if (bl2) {
                    lv3 = arg5.getUnderwaterMaterial();
                }
                if (s < n && bl3) {
                    lv3 = arg4;
                }
                r = q;
                if (s >= n - 1) {
                    arg.setBlockState(lv, lv3, false);
                    continue;
                }
                arg.setBlockState(lv, lv2, false);
                continue;
            }
            if (r <= 0) continue;
            --r;
            arg.setBlockState(lv, lv2, false);
        }
    }

    @Override
    public void initSeed(long l) {
        if (this.seed != l || this.field_22203 == null) {
            this.field_22203 = new OctavePerlinNoiseSampler(new ChunkRandom(l), (List<Integer>)ImmutableList.of((Object)0));
        }
        this.seed = l;
    }
}

