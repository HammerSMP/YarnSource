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
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class CountRangeDecorator
extends SimpleDecorator<RangeDecoratorConfig> {
    public CountRangeDecorator(Codec<RangeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, RangeDecoratorConfig arg, BlockPos arg2) {
        return IntStream.range(0, arg.count).mapToObj(i -> {
            int j = random.nextInt(16) + arg2.getX();
            int k = random.nextInt(16) + arg2.getZ();
            int l = random.nextInt(arg2.maximum - arg2.topOffset) + arg2.bottomOffset;
            return new BlockPos(j, l, k);
        });
    }
}

