/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BasaltColumnsFeature
extends Feature<BasaltColumnsFeatureConfig> {
    private static final ImmutableList<Block> field_24132 = ImmutableList.of((Object)Blocks.LAVA, (Object)Blocks.BEDROCK, (Object)Blocks.MAGMA_BLOCK, (Object)Blocks.SOUL_SAND, (Object)Blocks.NETHER_BRICKS, (Object)Blocks.NETHER_BRICK_FENCE, (Object)Blocks.NETHER_BRICK_STAIRS, (Object)Blocks.NETHER_WART, (Object)Blocks.CHEST, (Object)Blocks.SPAWNER);

    public BasaltColumnsFeature(Codec<BasaltColumnsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, BasaltColumnsFeatureConfig arg5) {
        int i = arg3.getSeaLevel();
        BlockPos lv = BasaltColumnsFeature.method_27094(arg, i, arg4.mutableCopy().method_27158(Direction.Axis.Y, 1, arg.getHeight() - 1), Integer.MAX_VALUE);
        if (lv == null) {
            return false;
        }
        int j = BasaltColumnsFeature.method_27099(random, arg5);
        boolean bl = random.nextFloat() < 0.9f;
        int k = Math.min(j, bl ? 5 : 8);
        int l = bl ? 50 : 15;
        boolean bl2 = false;
        for (BlockPos lv2 : BlockPos.method_27156(random, l, lv.getX() - k, lv.getY(), lv.getZ() - k, lv.getX() + k, lv.getY(), lv.getZ() + k)) {
            int m = j - lv2.getManhattanDistance(lv);
            if (m < 0) continue;
            bl2 |= this.method_27096(arg, i, lv2, m, BasaltColumnsFeature.method_27100(random, arg5));
        }
        return bl2;
    }

    private boolean method_27096(WorldAccess arg, int i, BlockPos arg2, int j, int k) {
        boolean bl = false;
        block0: for (BlockPos lv : BlockPos.iterate(arg2.getX() - k, arg2.getY(), arg2.getZ() - k, arg2.getX() + k, arg2.getY(), arg2.getZ() + k)) {
            BlockPos lv2;
            int l = lv.getManhattanDistance(arg2);
            BlockPos blockPos = lv2 = BasaltColumnsFeature.method_27095(arg, i, lv) ? BasaltColumnsFeature.method_27094(arg, i, lv.mutableCopy(), l) : BasaltColumnsFeature.method_27098(arg, lv.mutableCopy(), l);
            if (lv2 == null) continue;
            BlockPos.Mutable lv3 = lv2.mutableCopy();
            for (int m = j - l / 2; m >= 0; --m) {
                if (BasaltColumnsFeature.method_27095(arg, i, lv3)) {
                    this.setBlockState(arg, lv3, Blocks.BASALT.getDefaultState());
                    lv3.move(Direction.UP);
                    bl = true;
                    continue;
                }
                if (!arg.getBlockState(lv3).isOf(Blocks.BASALT)) continue block0;
                lv3.move(Direction.UP);
            }
        }
        return bl;
    }

    @Nullable
    private static BlockPos method_27094(WorldAccess arg, int i, BlockPos.Mutable arg2, int j) {
        while (arg2.getY() > 1 && j > 0) {
            --j;
            if (BasaltColumnsFeature.method_27095(arg, i, arg2)) {
                BlockState lv = arg.getBlockState(arg2.move(Direction.DOWN));
                arg2.move(Direction.UP);
                if (!lv.isAir() && !field_24132.contains((Object)lv.getBlock())) {
                    return arg2;
                }
            }
            arg2.move(Direction.DOWN);
        }
        return null;
    }

    @Nullable
    private static BlockPos method_27098(WorldAccess arg, BlockPos.Mutable arg2, int i) {
        while (arg2.getY() < arg.getHeight() && i > 0) {
            --i;
            BlockState lv = arg.getBlockState(arg2);
            if (field_24132.contains((Object)lv.getBlock())) {
                return null;
            }
            if (lv.isAir()) {
                return arg2;
            }
            arg2.move(Direction.UP);
        }
        return null;
    }

    private static int method_27099(Random random, BasaltColumnsFeatureConfig arg) {
        return arg.minHeight + random.nextInt(arg.maxHeight - arg.minHeight + 1);
    }

    private static int method_27100(Random random, BasaltColumnsFeatureConfig arg) {
        return arg.minReach + random.nextInt(arg.maxReach - arg.minReach + 1);
    }

    private static boolean method_27095(WorldAccess arg, int i, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        return lv.isAir() || lv.isOf(Blocks.LAVA) && arg2.getY() <= i;
    }
}

