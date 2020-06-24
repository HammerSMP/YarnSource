/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class ErodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public ErodedBadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        double e = 0.0;
        double f = Math.min(Math.abs(d), this.heightCutoffNoise.sample((double)i * 0.25, (double)j * 0.25, false) * 15.0);
        if (f > 0.0) {
            double g = 0.001953125;
            e = f * f * 2.5;
            double h = Math.abs(this.heightNoise.sample((double)i * 0.001953125, (double)j * 0.001953125, false));
            double n = Math.ceil(h * 50.0) + 14.0;
            if (e > n) {
                e = n;
            }
            e += 64.0;
        }
        int o = i & 0xF;
        int p = j & 0xF;
        BlockState lv = WHITE_TERRACOTTA;
        BlockState lv2 = arg2.getSurfaceConfig().getUnderMaterial();
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
        int r = -1;
        boolean bl2 = false;
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int s = Math.max(k, (int)e + 1); s >= 0; --s) {
            BlockState lv4;
            lv3.set(o, s, p);
            if (arg.getBlockState(lv3).isAir() && s < (int)e) {
                arg.setBlockState(lv3, arg3, false);
            }
            if ((lv4 = arg.getBlockState(lv3)).isAir()) {
                r = -1;
                continue;
            }
            if (!lv4.isOf(arg3.getBlock())) continue;
            if (r == -1) {
                bl2 = false;
                if (q <= 0) {
                    lv = Blocks.AIR.getDefaultState();
                    lv2 = arg3;
                } else if (s >= l - 4 && s <= l + 1) {
                    lv = WHITE_TERRACOTTA;
                    lv2 = arg2.getSurfaceConfig().getUnderMaterial();
                }
                if (s < l && (lv == null || lv.isAir())) {
                    lv = arg4;
                }
                r = q + Math.max(0, s - l);
                if (s >= l - 1) {
                    if (s > l + 3 + q) {
                        BlockState lv7;
                        if (s < 64 || s > 127) {
                            BlockState lv5 = ORANGE_TERRACOTTA;
                        } else if (bl) {
                            BlockState lv6 = TERRACOTTA;
                        } else {
                            lv7 = this.calculateLayerBlockState(i, s, j);
                        }
                        arg.setBlockState(lv3, lv7, false);
                        continue;
                    }
                    arg.setBlockState(lv3, arg2.getSurfaceConfig().getTopMaterial(), false);
                    bl2 = true;
                    continue;
                }
                arg.setBlockState(lv3, lv2, false);
                Block lv8 = lv2.getBlock();
                if (lv8 != Blocks.WHITE_TERRACOTTA && lv8 != Blocks.ORANGE_TERRACOTTA && lv8 != Blocks.MAGENTA_TERRACOTTA && lv8 != Blocks.LIGHT_BLUE_TERRACOTTA && lv8 != Blocks.YELLOW_TERRACOTTA && lv8 != Blocks.LIME_TERRACOTTA && lv8 != Blocks.PINK_TERRACOTTA && lv8 != Blocks.GRAY_TERRACOTTA && lv8 != Blocks.LIGHT_GRAY_TERRACOTTA && lv8 != Blocks.CYAN_TERRACOTTA && lv8 != Blocks.PURPLE_TERRACOTTA && lv8 != Blocks.BLUE_TERRACOTTA && lv8 != Blocks.BROWN_TERRACOTTA && lv8 != Blocks.GREEN_TERRACOTTA && lv8 != Blocks.RED_TERRACOTTA && lv8 != Blocks.BLACK_TERRACOTTA) continue;
                arg.setBlockState(lv3, ORANGE_TERRACOTTA, false);
                continue;
            }
            if (r <= 0) continue;
            --r;
            if (bl2) {
                arg.setBlockState(lv3, ORANGE_TERRACOTTA, false);
                continue;
            }
            arg.setBlockState(lv3, this.calculateLayerBlockState(i, s, j), false);
        }
    }
}

