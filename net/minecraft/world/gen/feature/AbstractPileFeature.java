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
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class AbstractPileFeature
extends Feature<BlockPileFeatureConfig> {
    public AbstractPileFeature(Codec<BlockPileFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, BlockPileFeatureConfig arg5) {
        if (arg4.getY() < 5) {
            return false;
        }
        int i = 2 + random.nextInt(2);
        int j = 2 + random.nextInt(2);
        for (BlockPos lv : BlockPos.iterate(arg4.add(-i, 0, -j), arg4.add(i, 1, j))) {
            int l;
            int k = arg4.getX() - lv.getX();
            if ((float)(k * k + (l = arg4.getZ() - lv.getZ()) * l) <= random.nextFloat() * 10.0f - random.nextFloat() * 6.0f) {
                this.addPileBlock(arg, lv, random, arg5);
                continue;
            }
            if (!((double)random.nextFloat() < 0.031)) continue;
            this.addPileBlock(arg, lv, random, arg5);
        }
        return true;
    }

    private boolean canPlacePileBlock(WorldAccess arg, BlockPos arg2, Random random) {
        BlockPos lv = arg2.down();
        BlockState lv2 = arg.getBlockState(lv);
        if (lv2.isOf(Blocks.GRASS_PATH)) {
            return random.nextBoolean();
        }
        return lv2.isSideSolidFullSquare(arg, lv, Direction.UP);
    }

    private void addPileBlock(WorldAccess arg, BlockPos arg2, Random random, BlockPileFeatureConfig arg3) {
        if (arg.isAir(arg2) && this.canPlacePileBlock(arg, arg2, random)) {
            arg.setBlockState(arg2, arg3.stateProvider.getBlockState(random, arg2), 4);
        }
    }
}

