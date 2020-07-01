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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeature
extends Feature<RandomFeatureConfig> {
    public RandomFeature(Codec<RandomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, RandomFeatureConfig arg4) {
        for (RandomFeatureEntry<?> lv : arg4.features) {
            if (!(random.nextFloat() < lv.chance)) continue;
            return lv.generate(arg, arg2, random, arg3);
        }
        return arg4.defaultFeature.generate(arg, arg2, random, arg3);
    }
}

