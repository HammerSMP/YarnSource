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
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;

public class GrassBlock
extends SpreadableBlock
implements Fertilizable {
    public GrassBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return arg.getBlockState(arg2.up()).isAir();
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        BlockPos lv = arg2.up();
        BlockState lv2 = Blocks.GRASS.getDefaultState();
        block0: for (int i = 0; i < 128; ++i) {
            BlockState lv7;
            BlockPos lv3 = lv;
            for (int j = 0; j < i / 16; ++j) {
                if (!arg.getBlockState((lv3 = lv3.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).down()).isOf(this) || arg.getBlockState(lv3).isFullCube(arg, lv3)) continue block0;
            }
            BlockState lv4 = arg.getBlockState(lv3);
            if (lv4.isOf(lv2.getBlock()) && random.nextInt(10) == 0) {
                ((Fertilizable)((Object)lv2.getBlock())).grow(arg, random, lv3, lv4);
            }
            if (!lv4.isAir()) continue;
            if (random.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = arg.getBiome(lv3).getFlowerFeatures();
                if (list.isEmpty()) continue;
                ConfiguredFeature<?, ?> lv5 = ((DecoratedFeatureConfig)list.get((int)0).config).feature.get();
                BlockState lv6 = ((FlowerFeature)lv5.feature).getFlowerState(random, lv3, lv5.config);
            } else {
                lv7 = lv2;
            }
            if (!lv7.canPlaceAt(arg, lv3)) continue;
            arg.setBlockState(lv3, lv7, 3);
        }
    }
}

