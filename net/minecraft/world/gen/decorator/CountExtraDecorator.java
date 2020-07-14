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
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class CountExtraDecorator
extends SimpleDecorator<CountExtraChanceDecoratorConfig> {
    public CountExtraDecorator(Codec<CountExtraChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountExtraChanceDecoratorConfig arg, BlockPos arg2) {
        int i2 = arg.count + (random.nextFloat() < arg.extraChance ? arg.extraCount : 0);
        return IntStream.range(0, i2).mapToObj(i -> arg2);
    }
}

