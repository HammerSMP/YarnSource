/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class BambooFeature
extends Feature<ProbabilityConfig> {
    private static final BlockState BAMBOO = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.getDefaultState().with(BambooBlock.AGE, 1)).with(BambooBlock.LEAVES, BambooLeaves.NONE)).with(BambooBlock.STAGE, 0);
    private static final BlockState BAMBOO_TOP_1 = (BlockState)((BlockState)BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE)).with(BambooBlock.STAGE, 1);
    private static final BlockState BAMBOO_TOP_2 = (BlockState)BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_TOP_3 = (BlockState)BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.SMALL);

    public BambooFeature(Codec<ProbabilityConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, ProbabilityConfig arg4) {
        int i = 0;
        BlockPos.Mutable lv = arg3.mutableCopy();
        BlockPos.Mutable lv2 = arg3.mutableCopy();
        if (arg.isAir(lv)) {
            if (Blocks.BAMBOO.getDefaultState().canPlaceAt(arg, lv)) {
                int j = random.nextInt(12) + 5;
                if (random.nextFloat() < arg4.probability) {
                    int k = random.nextInt(4) + 1;
                    for (int l = arg3.getX() - k; l <= arg3.getX() + k; ++l) {
                        for (int m = arg3.getZ() - k; m <= arg3.getZ() + k; ++m) {
                            int o;
                            int n = l - arg3.getX();
                            if (n * n + (o = m - arg3.getZ()) * o > k * k) continue;
                            lv2.set(l, arg.getTopY(Heightmap.Type.WORLD_SURFACE, l, m) - 1, m);
                            if (!BambooFeature.isSoil(arg.getBlockState(lv2).getBlock())) continue;
                            arg.setBlockState(lv2, Blocks.PODZOL.getDefaultState(), 2);
                        }
                    }
                }
                for (int p = 0; p < j && arg.isAir(lv); ++p) {
                    arg.setBlockState(lv, BAMBOO, 2);
                    lv.move(Direction.UP, 1);
                }
                if (lv.getY() - arg3.getY() >= 3) {
                    arg.setBlockState(lv, BAMBOO_TOP_1, 2);
                    arg.setBlockState(lv.move(Direction.DOWN, 1), BAMBOO_TOP_2, 2);
                    arg.setBlockState(lv.move(Direction.DOWN, 1), BAMBOO_TOP_3, 2);
                }
            }
            ++i;
        }
        return i > 0;
    }
}

