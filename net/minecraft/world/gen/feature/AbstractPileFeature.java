/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class AbstractPileFeature
extends Feature<BlockPileFeatureConfig> {
    public AbstractPileFeature(Codec<BlockPileFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, BlockPileFeatureConfig arg4) {
        if (arg3.getY() < 5) {
            return false;
        }
        int i = 2 + random.nextInt(2);
        int j = 2 + random.nextInt(2);
        for (BlockPos lv : BlockPos.iterate(arg3.add(-i, 0, -j), arg3.add(i, 1, j))) {
            int l;
            int k = arg3.getX() - lv.getX();
            if ((float)(k * k + (l = arg3.getZ() - lv.getZ()) * l) <= random.nextFloat() * 10.0f - random.nextFloat() * 6.0f) {
                this.addPileBlock(arg, lv, random, arg4);
                continue;
            }
            if (!((double)random.nextFloat() < 0.031)) continue;
            this.addPileBlock(arg, lv, random, arg4);
        }
        return true;
    }

    private boolean canPlacePileBlock(WorldAccess world, BlockPos pos, Random random) {
        BlockPos lv = pos.down();
        BlockState lv2 = world.getBlockState(lv);
        if (lv2.isOf(Blocks.GRASS_PATH)) {
            return random.nextBoolean();
        }
        return lv2.isSideSolidFullSquare(world, lv, Direction.UP);
    }

    private void addPileBlock(WorldAccess world, BlockPos pos, Random random, BlockPileFeatureConfig config) {
        if (world.isAir(pos) && this.canPlacePileBlock(world, pos, random)) {
            world.setBlockState(pos, config.stateProvider.getBlockState(random, pos), 4);
        }
    }
}

