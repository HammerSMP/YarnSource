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
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public abstract class CoralFeature
extends Feature<DefaultFeatureConfig> {
    public CoralFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DefaultFeatureConfig arg4) {
        BlockState lv = ((Block)BlockTags.CORAL_BLOCKS.getRandom(random)).getDefaultState();
        return this.spawnCoral(arg, random, arg3, lv);
    }

    protected abstract boolean spawnCoral(WorldAccess var1, Random var2, BlockPos var3, BlockState var4);

    protected boolean spawnCoralPiece(WorldAccess arg, Random random, BlockPos arg2, BlockState arg3) {
        BlockPos lv = arg2.up();
        BlockState lv2 = arg.getBlockState(arg2);
        if (!lv2.isOf(Blocks.WATER) && !lv2.isIn(BlockTags.CORALS) || !arg.getBlockState(lv).isOf(Blocks.WATER)) {
            return false;
        }
        arg.setBlockState(arg2, arg3, 3);
        if (random.nextFloat() < 0.25f) {
            arg.setBlockState(lv, ((Block)BlockTags.CORALS.getRandom(random)).getDefaultState(), 2);
        } else if (random.nextFloat() < 0.05f) {
            arg.setBlockState(lv, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
        }
        for (Direction lv3 : Direction.Type.HORIZONTAL) {
            BlockPos lv4;
            if (!(random.nextFloat() < 0.2f) || !arg.getBlockState(lv4 = arg2.offset(lv3)).isOf(Blocks.WATER)) continue;
            BlockState lv5 = (BlockState)((Block)BlockTags.WALL_CORALS.getRandom(random)).getDefaultState().with(DeadCoralWallFanBlock.FACING, lv3);
            arg.setBlockState(lv4, lv5, 2);
        }
        return true;
    }
}

