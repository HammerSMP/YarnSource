/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class RandomCountRangeDecorator
extends SimpleDecorator<RangeDecoratorConfig> {
    public RandomCountRangeDecorator(Function<Dynamic<?>, ? extends RangeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, RangeDecoratorConfig arg, BlockPos arg2) {
        int i2 = random.nextInt(Math.max(arg.count, 1));
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16) + arg2.getX();
            int k = random.nextInt(16) + arg2.getZ();
            int l = random.nextInt(arg2.maximum - arg2.topOffset) + arg2.bottomOffset;
            return new BlockPos(j, l, k);
        });
    }
}

