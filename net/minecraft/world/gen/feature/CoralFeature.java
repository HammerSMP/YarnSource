/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public abstract class CoralFeature
extends Feature<DefaultFeatureConfig> {
    public CoralFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        BlockState lv = ((Block)BlockTags.CORAL_BLOCKS.getRandom(random)).getDefaultState();
        return this.spawnCoral(arg, random, arg4, lv);
    }

    protected abstract boolean spawnCoral(IWorld var1, Random var2, BlockPos var3, BlockState var4);

    protected boolean spawnCoralPiece(IWorld arg, Random random, BlockPos arg2, BlockState arg3) {
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

