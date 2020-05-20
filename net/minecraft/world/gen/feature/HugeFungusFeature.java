/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
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
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, HugeFungusFeatureConfig arg5) {
        Block lv = arg5.validBaseBlock.getBlock();
        BlockPos lv2 = null;
        if (arg5.planted) {
            Block lv3 = arg.getBlockState(arg4.down()).getBlock();
            if (lv3 == lv) {
                lv2 = arg4;
            }
        } else {
            lv2 = HugeFungusFeature.getStartPos(arg, arg4, lv);
        }
        if (lv2 == null) {
            return false;
        }
        int i = MathHelper.nextInt(random, 4, 13);
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        if (!arg5.planted) {
            int j = arg.getDimensionHeight();
            if (lv2.getY() + i + 1 >= j) {
                return false;
            }
        }
        boolean bl = !arg5.planted && random.nextFloat() < 0.06f;
        arg.setBlockState(arg4, Blocks.AIR.getDefaultState(), 4);
        this.generateHat(arg, random, arg5, lv2, i, bl);
        this.generateStem(arg, random, arg5, lv2, i, bl);
        return true;
    }

    public static boolean method_24866(WorldAccess arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> {
            Material lv = arg.getMaterial();
            return lv == Material.REPLACEABLE_PLANT;
        });
    }

    private static boolean method_24868(WorldAccess arg, BlockPos arg2) {
        return arg.getBlockState(arg2).isAir() || !arg.getFluidState(arg2).isEmpty() || HugeFungusFeature.method_24866(arg, arg2);
    }

    private void generateStem(WorldAccess arg, Random random, HugeFungusFeatureConfig arg2, BlockPos arg3, int i, boolean bl) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        BlockState lv2 = arg2.stemState;
        int j = bl ? 1 : 0;
        for (int k = -j; k <= j; ++k) {
            for (int l = -j; l <= j; ++l) {
                boolean bl2 = bl && MathHelper.abs(k) == j && MathHelper.abs(l) == j;
                for (int m = 0; m < i; ++m) {
                    lv.set(arg3, k, m, l);
                    if (!HugeFungusFeature.method_24868(arg, lv)) continue;
                    if (arg2.planted) {
                        if (!arg.getBlockState((BlockPos)lv.down()).isAir()) {
                            arg.breakBlock(lv, true);
                        }
                        arg.setBlockState(lv, lv2, 3);
                        continue;
                    }
                    if (bl2) {
                        if (!(random.nextFloat() < 0.1f)) continue;
                        this.setBlockState(arg, lv, lv2);
                        continue;
                    }
                    this.setBlockState(arg, lv, lv2);
                }
            }
        }
    }

    private void generateHat(WorldAccess arg, Random random, HugeFungusFeatureConfig arg2, BlockPos arg3, int i, boolean bl) {
        int k;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        boolean bl2 = arg2.hatState.isOf(Blocks.NETHER_WART_BLOCK);
        int j = Math.min(random.nextInt(1 + i / 3) + 5, i);
        for (int l = k = i - j; l <= i; ++l) {
            int m;
            int n = m = l < i - random.nextInt(3) ? 2 : 1;
            if (j > 8 && l < k + 4) {
                m = 3;
            }
            if (bl) {
                ++m;
            }
            for (int n2 = -m; n2 <= m; ++n2) {
                for (int o = -m; o <= m; ++o) {
                    boolean bl3 = n2 == -m || n2 == m;
                    boolean bl4 = o == -m || o == m;
                    boolean bl5 = !bl3 && !bl4 && l != i;
                    boolean bl6 = bl3 && bl4;
                    boolean bl7 = l < k + 3;
                    lv.set(arg3, n2, l, o);
                    if (!HugeFungusFeature.method_24868(arg, lv)) continue;
                    if (arg2.planted && !arg.getBlockState((BlockPos)lv.down()).isAir()) {
                        arg.breakBlock(lv, true);
                    }
                    if (bl7) {
                        if (bl5) continue;
                        this.tryGenerateVines(arg, random, lv, arg2.hatState, bl2);
                        continue;
                    }
                    if (bl5) {
                        this.generateHatBlock(arg, random, arg2, lv, 0.1f, 0.2f, bl2 ? 0.1f : 0.0f);
                        continue;
                    }
                    if (bl6) {
                        this.generateHatBlock(arg, random, arg2, lv, 0.01f, 0.7f, bl2 ? 0.083f : 0.0f);
                        continue;
                    }
                    this.generateHatBlock(arg, random, arg2, lv, 5.0E-4f, 0.98f, bl2 ? 0.07f : 0.0f);
                }
            }
        }
    }

    private void generateHatBlock(WorldAccess arg, Random random, HugeFungusFeatureConfig arg2, BlockPos.Mutable arg3, float f, float g, float h) {
        if (random.nextFloat() < f) {
            this.setBlockState(arg, arg3, arg2.decorationState);
        } else if (random.nextFloat() < g) {
            this.setBlockState(arg, arg3, arg2.hatState);
            if (random.nextFloat() < h) {
                HugeFungusFeature.generateVines(arg3, arg, random);
            }
        }
    }

    private void tryGenerateVines(WorldAccess arg, Random random, BlockPos arg2, BlockState arg3, boolean bl) {
        if (arg.getBlockState(arg2.down()).isOf(arg3.getBlock())) {
            this.setBlockState(arg, arg2, arg3);
        } else if ((double)random.nextFloat() < 0.15) {
            this.setBlockState(arg, arg2, arg3);
            if (bl && random.nextInt(11) == 0) {
                HugeFungusFeature.generateVines(arg2, arg, random);
            }
        }
    }

    @Nullable
    private static BlockPos.Mutable getStartPos(WorldAccess arg, BlockPos arg2, Block arg3) {
        BlockPos.Mutable lv = arg2.mutableCopy();
        for (int i = arg2.getY(); i >= 1; --i) {
            lv.setY(i);
            Block lv2 = arg.getBlockState((BlockPos)lv.down()).getBlock();
            if (lv2 != arg3) continue;
            return lv;
        }
        return null;
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

