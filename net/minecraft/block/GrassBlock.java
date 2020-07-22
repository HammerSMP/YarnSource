/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowerFeature;

public class GrassBlock
extends SpreadableBlock
implements Fertilizable {
    public GrassBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return world.getBlockState(pos.up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos lv = pos.up();
        BlockState lv2 = Blocks.GRASS.getDefaultState();
        block0: for (int i = 0; i < 128; ++i) {
            BlockState lv8;
            BlockPos lv3 = lv;
            for (int j = 0; j < i / 16; ++j) {
                if (!world.getBlockState((lv3 = lv3.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).down()).isOf(this) || world.getBlockState(lv3).isFullCube(world, lv3)) continue block0;
            }
            BlockState lv4 = world.getBlockState(lv3);
            if (lv4.isOf(lv2.getBlock()) && random.nextInt(10) == 0) {
                ((Fertilizable)((Object)lv2.getBlock())).grow(world, random, lv3, lv4);
            }
            if (!lv4.isAir()) continue;
            if (random.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = world.getBiome(lv3).getFlowerFeatures();
                if (list.isEmpty()) continue;
                ConfiguredFeature<?, ?> lv5 = list.get(0);
                FlowerFeature lv6 = (FlowerFeature)lv5.feature;
                BlockState lv7 = lv6.getFlowerState(random, lv3, lv5.getConfig());
            } else {
                lv8 = lv2;
            }
            if (!lv8.canPlaceAt(world, lv3)) continue;
            world.setBlockState(lv3, lv8, 3);
        }
    }
}

