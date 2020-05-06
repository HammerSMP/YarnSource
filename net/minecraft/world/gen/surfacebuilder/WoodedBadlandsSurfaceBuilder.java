/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class WoodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public WoodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        int n = i & 0xF;
        int o = j & 0xF;
        BlockState lv = WHITE_TERRACOTTA;
        BlockState lv2 = arg2.getSurfaceConfig().getUnderMaterial();
        int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
        int q = -1;
        boolean bl2 = false;
        int r = 0;
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int s = k; s >= 0; --s) {
            if (r >= 15) continue;
            lv3.set(n, s, o);
            BlockState lv4 = arg.getBlockState(lv3);
            if (lv4.isAir()) {
                q = -1;
                continue;
            }
            if (!lv4.isOf(arg3.getBlock())) continue;
            if (q == -1) {
                bl2 = false;
                if (p <= 0) {
                    lv = Blocks.AIR.getDefaultState();
                    lv2 = arg3;
                } else if (s >= l - 4 && s <= l + 1) {
                    lv = WHITE_TERRACOTTA;
                    lv2 = arg2.getSurfaceConfig().getUnderMaterial();
                }
                if (s < l && (lv == null || lv.isAir())) {
                    lv = arg4;
                }
                q = p + Math.max(0, s - l);
                if (s >= l - 1) {
                    if (s > 86 + p * 2) {
                        if (bl) {
                            arg.setBlockState(lv3, Blocks.COARSE_DIRT.getDefaultState(), false);
                        } else {
                            arg.setBlockState(lv3, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        }
                    } else if (s > l + 3 + p) {
                        BlockState lv7;
                        if (s < 64 || s > 127) {
                            BlockState lv5 = ORANGE_TERRACOTTA;
                        } else if (bl) {
                            BlockState lv6 = TERRACOTTA;
                        } else {
                            lv7 = this.calculateLayerBlockState(i, s, j);
                        }
                        arg.setBlockState(lv3, lv7, false);
                    } else {
                        arg.setBlockState(lv3, arg2.getSurfaceConfig().getTopMaterial(), false);
                        bl2 = true;
                    }
                } else {
                    arg.setBlockState(lv3, lv2, false);
                    if (lv2 == WHITE_TERRACOTTA) {
                        arg.setBlockState(lv3, ORANGE_TERRACOTTA, false);
                    }
                }
            } else if (q > 0) {
                --q;
                if (bl2) {
                    arg.setBlockState(lv3, ORANGE_TERRACOTTA, false);
                } else {
                    arg.setBlockState(lv3, this.calculateLayerBlockState(i, s, j), false);
                }
            }
            ++r;
        }
    }
}

