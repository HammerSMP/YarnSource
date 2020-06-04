/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.NetherForestVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVinesFeature;

public class NyliumBlock
extends Block
implements Fertilizable {
    protected NyliumBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    private static boolean stayAlive(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.up();
        BlockState lv2 = arg2.getBlockState(lv);
        int i = ChunkLightProvider.getRealisticOpacity(arg2, arg, arg3, lv2, lv, Direction.UP, lv2.getOpacity(arg2, lv));
        return i < arg2.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!NyliumBlock.stayAlive(arg, arg2, arg3)) {
            arg2.setBlockState(arg3, Blocks.NETHERRACK.getDefaultState());
        }
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
        BlockState lv = arg.getBlockState(arg2);
        BlockPos lv2 = arg2.up();
        if (lv.isOf(Blocks.CRIMSON_NYLIUM)) {
            NetherForestVegetationFeature.method_26264(arg, random, lv2, DefaultBiomeFeatures.CRIMSON_ROOTS_CONFIG, 3, 1);
        } else if (lv.isOf(Blocks.WARPED_NYLIUM)) {
            NetherForestVegetationFeature.method_26264(arg, random, lv2, DefaultBiomeFeatures.WARPED_ROOTS_CONFIG, 3, 1);
            NetherForestVegetationFeature.method_26264(arg, random, lv2, DefaultBiomeFeatures.NETHER_SPROUTS_CONFIG, 3, 1);
            if (random.nextInt(8) == 0) {
                TwistingVinesFeature.method_26265(arg, random, lv2, 3, 1, 2);
            }
        }
    }
}

