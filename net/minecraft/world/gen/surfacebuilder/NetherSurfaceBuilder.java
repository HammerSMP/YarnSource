/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class NetherSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState GLOWSTONE = Blocks.SOUL_SAND.getDefaultState();
    protected long seed;
    protected OctavePerlinNoiseSampler noise;

    public NetherSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        int n = l;
        int o = i & 0xF;
        int p = j & 0xF;
        double e = 0.03125;
        boolean bl = this.noise.sample((double)i * 0.03125, (double)j * 0.03125, 0.0) * 75.0 + random.nextDouble() > 0.0;
        boolean bl2 = this.noise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int r = -1;
        BlockState lv2 = arg5.getTopMaterial();
        BlockState lv3 = arg5.getUnderMaterial();
        for (int s = 127; s >= 0; --s) {
            lv.set(o, s, p);
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
                    lv3 = arg5.getUnderMaterial();
                } else if (s >= n - 4 && s <= n + 1) {
                    lv2 = arg5.getTopMaterial();
                    lv3 = arg5.getUnderMaterial();
                    if (bl2) {
                        lv2 = GRAVEL;
                        lv3 = arg5.getUnderMaterial();
                    }
                    if (bl) {
                        lv2 = GLOWSTONE;
                        lv3 = GLOWSTONE;
                    }
                }
                if (s < n && bl3) {
                    lv2 = arg4;
                }
                r = q;
                if (s >= n - 1) {
                    arg.setBlockState(lv, lv2, false);
                    continue;
                }
                arg.setBlockState(lv, lv3, false);
                continue;
            }
            if (r <= 0) continue;
            --r;
            arg.setBlockState(lv, lv3, false);
        }
    }

    @Override
    public void initSeed(long l) {
        if (this.seed != l || this.noise == null) {
            this.noise = new OctavePerlinNoiseSampler(new ChunkRandom(l), IntStream.rangeClosed(-3, 0));
        }
        this.seed = l;
    }
}

