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
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, BasaltColumnsFeatureConfig arg4) {
        int i = arg2.getSeaLevel();
        if (!BasaltColumnsFeature.method_30379(arg, i, arg3.mutableCopy())) {
            return false;
        }
        int j = arg4.method_30394().method_30321(random);
        boolean bl = random.nextFloat() < 0.9f;
        int k = Math.min(j, bl ? 5 : 8);
        int l = bl ? 50 : 15;
        boolean bl2 = false;
        for (BlockPos lv : BlockPos.method_27156(random, l, arg3.getX() - k, arg3.getY(), arg3.getZ() - k, arg3.getX() + k, arg3.getY(), arg3.getZ() + k)) {
            int m = j - lv.getManhattanDistance(arg3);
            if (m < 0) continue;
            bl2 |= this.method_27096(arg, i, lv, m, arg4.method_30391().method_30321(random));
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
            if (BasaltColumnsFeature.method_30379(arg, i, arg2)) {
                return arg2;
            }
            arg2.move(Direction.DOWN);
        }
        return null;
    }

    private static boolean method_30379(WorldAccess arg, int i, BlockPos.Mutable arg2) {
        if (BasaltColumnsFeature.method_27095(arg, i, arg2)) {
            BlockState lv = arg.getBlockState(arg2.move(Direction.DOWN));
            arg2.move(Direction.UP);
            return !lv.isAir() && !field_24132.contains((Object)lv.getBlock());
        }
        return false;
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

    private static boolean method_27095(WorldAccess arg, int i, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        return lv.isAir() || lv.isOf(Blocks.LAVA) && arg2.getY() <= i;
    }
}

