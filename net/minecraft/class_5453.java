/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.SimpleDecorator;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;

public class class_5453
extends SimpleDecorator<SeaPickleFeatureConfig> {
    public class_5453(Codec<SeaPickleFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, SeaPickleFeatureConfig arg, BlockPos arg2) {
        return IntStream.range(0, random.nextInt(random.nextInt(arg.method_30396().method_30321(random)) + 1)).mapToObj(i -> {
            int j = random.nextInt(16) + arg2.getX();
            int k = random.nextInt(16) + arg2.getZ();
            int l = random.nextInt(120) + 4;
            return new BlockPos(j, l, k);
        });
    }
}

