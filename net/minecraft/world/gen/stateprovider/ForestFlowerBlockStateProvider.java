/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class ForestFlowerBlockStateProvider
extends BlockStateProvider {
    public static final Codec<ForestFlowerBlockStateProvider> field_24940 = Codec.unit(() -> field_24941);
    private static final BlockState[] flowers = new BlockState[]{Blocks.DANDELION.getDefaultState(), Blocks.POPPY.getDefaultState(), Blocks.ALLIUM.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.ORANGE_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState(), Blocks.LILY_OF_THE_VALLEY.getDefaultState()};
    public static final ForestFlowerBlockStateProvider field_24941 = new ForestFlowerBlockStateProvider();

    @Override
    protected BlockStateProviderType<?> method_28862() {
        return BlockStateProviderType.FOREST_FLOWER_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos arg) {
        double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample((double)arg.getX() / 48.0, (double)arg.getZ() / 48.0, false)) / 2.0, 0.0, 0.9999);
        return flowers[(int)(d * (double)flowers.length)];
    }
}

