/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class DepthAverageDecorator
extends SimpleDecorator<CountDepthDecoratorConfig> {
    public DepthAverageDecorator(Codec<CountDepthDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountDepthDecoratorConfig arg, BlockPos arg2) {
        int i = arg.count;
        int j = arg.spread;
        int k = arg2.getX();
        int l = arg2.getZ();
        int m = random.nextInt(j) + random.nextInt(j) - j + i;
        return Stream.of(new BlockPos(k, m, l));
    }
}

