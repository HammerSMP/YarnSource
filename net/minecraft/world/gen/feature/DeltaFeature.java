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
import net.minecraft.world.gen.feature.DeltaFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DeltaFeature
extends Feature<DeltaFeatureConfig> {
    private static final ImmutableList<Block> field_24133 = ImmutableList.of((Object)Blocks.BEDROCK, (Object)Blocks.NETHER_BRICKS, (Object)Blocks.NETHER_BRICK_FENCE, (Object)Blocks.NETHER_BRICK_STAIRS, (Object)Blocks.NETHER_WART, (Object)Blocks.CHEST, (Object)Blocks.SPAWNER);
    private static final Direction[] field_23883 = Direction.values();

    private static int method_27104(Random random, DeltaFeatureConfig arg) {
        return arg.minRadius + random.nextInt(arg.maxRadius - arg.minRadius + 1);
    }

    private static int method_27105(Random random, DeltaFeatureConfig arg) {
        return random.nextInt(arg.maxRim + 1);
    }

    public DeltaFeature(Codec<DeltaFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DeltaFeatureConfig arg4) {
        BlockPos lv = DeltaFeature.method_27102(arg, arg3.mutableCopy().method_27158(Direction.Axis.Y, 1, arg.getHeight() - 1));
        if (lv == null) {
            return false;
        }
        boolean bl = false;
        boolean bl2 = random.nextDouble() < 0.9;
        int i = bl2 ? DeltaFeature.method_27105(random, arg4) : 0;
        int j = bl2 ? DeltaFeature.method_27105(random, arg4) : 0;
        boolean bl3 = bl2 && i != 0 && j != 0;
        int k = DeltaFeature.method_27104(random, arg4);
        int l = DeltaFeature.method_27104(random, arg4);
        int m = Math.max(k, l);
        for (BlockPos lv2 : BlockPos.iterateOutwards(lv, k, 0, l)) {
            BlockPos lv3;
            if (lv2.getManhattanDistance(lv) > m) break;
            if (!DeltaFeature.method_27103(arg, lv2, arg4)) continue;
            if (bl3) {
                bl = true;
                this.setBlockState(arg, lv2, arg4.rim);
            }
            if (!DeltaFeature.method_27103(arg, lv3 = lv2.add(i, 0, j), arg4)) continue;
            bl = true;
            this.setBlockState(arg, lv3, arg4.contents);
        }
        return bl;
    }

    private static boolean method_27103(WorldAccess arg, BlockPos arg2, DeltaFeatureConfig arg3) {
        BlockState lv = arg.getBlockState(arg2);
        if (lv.isOf(arg3.contents.getBlock())) {
            return false;
        }
        if (field_24133.contains((Object)lv.getBlock())) {
            return false;
        }
        for (Direction lv2 : field_23883) {
            boolean bl = arg.getBlockState(arg2.offset(lv2)).isAir();
            if ((!bl || lv2 == Direction.UP) && (bl || lv2 != Direction.UP)) continue;
            return false;
        }
        return true;
    }

    @Nullable
    private static BlockPos method_27102(WorldAccess arg, BlockPos.Mutable arg2) {
        while (arg2.getY() > 1) {
            if (arg.getBlockState(arg2).isAir()) {
                BlockState lv = arg.getBlockState(arg2.move(Direction.DOWN));
                arg2.move(Direction.UP);
                if (!(lv.isOf(Blocks.LAVA) || lv.isOf(Blocks.BEDROCK) || lv.isAir())) {
                    return arg2;
                }
            }
            arg2.move(Direction.DOWN);
        }
        return null;
    }
}

