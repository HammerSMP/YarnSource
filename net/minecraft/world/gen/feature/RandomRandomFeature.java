/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;

public class RandomRandomFeature
extends Feature<RandomRandomFeatureConfig> {
    public RandomRandomFeature(Codec<RandomRandomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, RandomRandomFeatureConfig arg4) {
        int i = random.nextInt(5) - 3 + arg4.count;
        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(arg4.features.size());
            ConfiguredFeature<?, ?> lv = arg4.features.get(k);
            lv.generate(arg, arg2, random, arg3);
        }
        return true;
    }
}

