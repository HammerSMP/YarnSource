/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class CountDepthAverageDecorator
extends SimpleDecorator<CountDepthDecoratorConfig> {
    public CountDepthAverageDecorator(Codec<CountDepthDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountDepthDecoratorConfig arg, BlockPos arg2) {
        int i = arg.count;
        int j = arg.baseline;
        int k2 = arg.spread;
        return IntStream.range(0, i).mapToObj(k -> {
            int l = random.nextInt(16) + arg2.getX();
            int m = random.nextInt(16) + arg2.getZ();
            int n = random.nextInt(k2) + random.nextInt(k2) - k2 + j;
            return new BlockPos(l, n, m);
        });
    }
}

