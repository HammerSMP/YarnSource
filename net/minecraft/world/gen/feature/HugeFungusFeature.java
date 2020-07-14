/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.WeepingVinesFeature;

public class HugeFungusFeature
extends Feature<HugeFungusFeatureConfig> {
    public HugeFungusFeature(Codec<HugeFungusFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, HugeFungusFeatureConfig arg4) {
        Block lv = arg4.validBaseBlock.getBlock();
        BlockPos lv2 = null;
        Block lv3 = arg.getBlockState(arg3.down()).getBlock();
        if (lv3 == lv) {
            lv2 = arg3;
        }
        if (lv2 == null) {
            return false;
        }
        int i = MathHelper.nextInt(random, 4, 13);
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        if (!arg4.planted) {
            int j = arg2.getMaxY();
            if (lv2.getY() + i + 1 >= j) {
                return false;
            }
        }
        boolean bl = !arg4.planted && random.nextFloat() < 0.06f;
        arg.setBlockState(arg3, Blocks.AIR.getDefaultState(), 4);
        this.generateStem(arg, random, arg4, lv2, i, bl);
        this.generateHat(arg, random, arg4, lv2, i, bl);
        return true;
    }

    private static boolean method_24866(WorldAccess arg2, BlockPos arg22, boolean bl) {
        return arg2.testBlockState(arg22, arg -> {
            Material lv = arg.getMaterial();
            return arg.getMaterial().isReplaceable() || bl && lv == Material.PLANT;
        });
    }

    private void generateStem(WorldAccess world, Random random, HugeFungusFeatureConfig config, BlockPos arg3, int stemHeight, boolean thickStem) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        BlockState lv2 = config.stemState;
        int j = thickStem ? 1 : 0;
        for (int k = -j; k <= j; ++k) {
            for (int l = -j; l <= j; ++l) {
                boolean bl2 = thickStem && MathHelper.abs(k) == j && MathHelper.abs(l) == j;
                for (int m = 0; m < stemHeight; ++m) {
                    lv.set(arg3, k, m, l);
                    if (!HugeFungusFeature.method_24866(world, lv, true)) continue;
                    if (config.planted) {
                        if (!world.getBlockState((BlockPos)lv.down()).isAir()) {
                            world.breakBlock(lv, true);
                        }
                        world.setBlockState(lv, lv2, 3);
                        continue;
                    }
                    if (bl2) {
                        if (!(random.nextFloat() < 0.1f)) continue;
                        this.setBlockState(world, lv, lv2);
                        continue;
                    }
                    this.setBlockState(world, lv, lv2);
                }
            }
        }
    }

    private void generateHat(WorldAccess world, Random random, HugeFungusFeatureConfig config, BlockPos arg3, int hatHeight, boolean thickStem) {
        int k;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        boolean bl2 = config.hatState.isOf(Blocks.NETHER_WART_BLOCK);
        int j = Math.min(random.nextInt(1 + hatHeight / 3) + 5, hatHeight);
        for (int l = k = hatHeight - j; l <= hatHeight; ++l) {
            int m;
            int n = m = l < hatHeight - random.nextInt(3) ? 2 : 1;
            if (j > 8 && l < k + 4) {
                m = 3;
            }
            if (thickStem) {
                ++m;
            }
            for (int n2 = -m; n2 <= m; ++n2) {
                for (int o = -m; o <= m; ++o) {
                    boolean bl3 = n2 == -m || n2 == m;
                    boolean bl4 = o == -m || o == m;
                    boolean bl5 = !bl3 && !bl4 && l != hatHeight;
                    boolean bl6 = bl3 && bl4;
                    boolean bl7 = l < k + 3;
                    lv.set(arg3, n2, l, o);
                    if (!HugeFungusFeature.method_24866(world, lv, false)) continue;
                    if (config.planted && !world.getBlockState((BlockPos)lv.down()).isAir()) {
                        world.breakBlock(lv, true);
                    }
                    if (bl7) {
                        if (bl5) continue;
                        this.tryGenerateVines(world, random, lv, config.hatState, bl2);
                        continue;
                    }
                    if (bl5) {
                        this.generateHatBlock(world, random, config, lv, 0.1f, 0.2f, bl2 ? 0.1f : 0.0f);
                        continue;
                    }
                    if (bl6) {
                        this.generateHatBlock(world, random, config, lv, 0.01f, 0.7f, bl2 ? 0.083f : 0.0f);
                        continue;
                    }
                    this.generateHatBlock(world, random, config, lv, 5.0E-4f, 0.98f, bl2 ? 0.07f : 0.0f);
                }
            }
        }
    }

    private void generateHatBlock(WorldAccess world, Random random, HugeFungusFeatureConfig config, BlockPos.Mutable pos, float decorationChance, float generationChance, float vineChance) {
        if (random.nextFloat() < decorationChance) {
            this.setBlockState(world, pos, config.decorationState);
        } else if (random.nextFloat() < generationChance) {
            this.setBlockState(world, pos, config.hatState);
            if (random.nextFloat() < vineChance) {
                HugeFungusFeature.generateVines(pos, world, random);
            }
        }
    }

    private void tryGenerateVines(WorldAccess world, Random random, BlockPos pos, BlockState state, boolean bl) {
        if (world.getBlockState(pos.down()).isOf(state.getBlock())) {
            this.setBlockState(world, pos, state);
        } else if ((double)random.nextFloat() < 0.15) {
            this.setBlockState(world, pos, state);
            if (bl && random.nextInt(11) == 0) {
                HugeFungusFeature.generateVines(pos, world, random);
            }
        }
    }

    private static void generateVines(BlockPos arg, WorldAccess arg2, Random random) {
        BlockPos.Mutable lv = arg.mutableCopy().move(Direction.DOWN);
        if (!arg2.isAir(lv)) {
            return;
        }
        int i = MathHelper.nextInt(random, 1, 5);
        if (random.nextInt(7) == 0) {
            i *= 2;
        }
        int j = 23;
        int k = 25;
        WeepingVinesFeature.generateVineColumn(arg2, random, lv, i, 23, 25);
    }
}

