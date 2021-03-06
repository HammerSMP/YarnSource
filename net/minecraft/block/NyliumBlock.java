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
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.NetherForestVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVinesFeature;

public class NyliumBlock
extends Block
implements Fertilizable {
    protected NyliumBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    private static boolean stayAlive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos lv = pos.up();
        BlockState lv2 = world.getBlockState(lv);
        int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, lv2, lv, Direction.UP, lv2.getOpacity(world, lv));
        return i < world.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!NyliumBlock.stayAlive(state, world, pos)) {
            world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
        }
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
        BlockState lv = world.getBlockState(pos);
        BlockPos lv2 = pos.up();
        if (lv.isOf(Blocks.CRIMSON_NYLIUM)) {
            NetherForestVegetationFeature.method_26264(world, random, lv2, ConfiguredFeatures.class_5465.field_26151, 3, 1);
        } else if (lv.isOf(Blocks.WARPED_NYLIUM)) {
            NetherForestVegetationFeature.method_26264(world, random, lv2, ConfiguredFeatures.class_5465.field_26152, 3, 1);
            NetherForestVegetationFeature.method_26264(world, random, lv2, ConfiguredFeatures.class_5465.field_26153, 3, 1);
            if (random.nextInt(8) == 0) {
                TwistingVinesFeature.method_26265(world, random, lv2, 3, 1, 2);
            }
        }
    }
}

