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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;

public class SeaPickleFeature
extends Feature<SeaPickleFeatureConfig> {
    public SeaPickleFeature(Function<Dynamic<?>, ? extends SeaPickleFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockPos arg4, SeaPickleFeatureConfig arg5) {
        int i = 0;
        for (int j = 0; j < arg5.count; ++j) {
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int m = arg.getTopY(Heightmap.Type.OCEAN_FLOOR, arg4.getX() + k, arg4.getZ() + l);
            BlockPos lv = new BlockPos(arg4.getX() + k, m, arg4.getZ() + l);
            BlockState lv2 = (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
            if (!arg.getBlockState(lv).isOf(Blocks.WATER) || !lv2.canPlaceAt(arg, lv)) continue;
            arg.setBlockState(lv, lv2, 2);
            ++i;
        }
        return i > 0;
    }
}

