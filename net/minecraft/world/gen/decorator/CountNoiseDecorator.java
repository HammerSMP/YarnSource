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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.decorator.CountNoiseDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class CountNoiseDecorator
extends Decorator<CountNoiseDecoratorConfig> {
    public CountNoiseDecorator(Codec<CountNoiseDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext arg, Random random, CountNoiseDecoratorConfig arg2, BlockPos arg3) {
        double d = Biome.FOLIAGE_NOISE.sample((double)arg3.getX() / 200.0, (double)arg3.getZ() / 200.0, false);
        int i2 = d < arg2.noiseLevel ? arg2.belowNoise : arg2.aboveNoise;
        return IntStream.range(0, i2).mapToObj(i -> arg3);
    }
}

