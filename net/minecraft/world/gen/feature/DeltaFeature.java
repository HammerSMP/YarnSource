/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
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

    public DeltaFeature(Codec<DeltaFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DeltaFeatureConfig arg4) {
        boolean bl = false;
        boolean bl2 = random.nextDouble() < 0.9;
        int i = bl2 ? arg4.method_30403().getValue(random) : 0;
        int j = bl2 ? arg4.method_30403().getValue(random) : 0;
        boolean bl3 = bl2 && i != 0 && j != 0;
        int k = arg4.method_30402().getValue(random);
        int l = arg4.method_30402().getValue(random);
        int m = Math.max(k, l);
        for (BlockPos lv : BlockPos.iterateOutwards(arg3, k, 0, l)) {
            BlockPos lv2;
            if (lv.getManhattanDistance(arg3) > m) break;
            if (!DeltaFeature.method_27103(arg, lv, arg4)) continue;
            if (bl3) {
                bl = true;
                this.setBlockState(arg, lv, arg4.method_30400());
            }
            if (!DeltaFeature.method_27103(arg, lv2 = lv.add(i, 0, j), arg4)) continue;
            bl = true;
            this.setBlockState(arg, lv2, arg4.method_30397());
        }
        return bl;
    }

    private static boolean method_27103(WorldAccess arg, BlockPos arg2, DeltaFeatureConfig arg3) {
        BlockState lv = arg.getBlockState(arg2);
        if (lv.isOf(arg3.method_30397().getBlock())) {
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
}

