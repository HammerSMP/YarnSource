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
import net.minecraft.world.gen.decorator.SimpleDecorator;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;

public class HeightmapNoiseBiasedDecorator
extends SimpleDecorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> {
    public HeightmapNoiseBiasedDecorator(Codec<TopSolidHeightmapNoiseBiasedDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, TopSolidHeightmapNoiseBiasedDecoratorConfig arg, BlockPos arg2) {
        double d = Biome.FOLIAGE_NOISE.sample((double)arg2.getX() / arg.noiseFactor, (double)arg2.getZ() / arg.noiseFactor, false);
        int i2 = (int)Math.ceil((d + arg.noiseOffset) * (double)arg.noiseToCountRatio);
        return IntStream.range(0, i2).mapToObj(i -> arg2);
    }
}

