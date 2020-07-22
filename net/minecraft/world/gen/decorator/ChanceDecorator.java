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
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class ChanceDecorator
extends SimpleDecorator<ChanceDecoratorConfig> {
    public ChanceDecorator(Codec<ChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, ChanceDecoratorConfig arg, BlockPos arg2) {
        if (random.nextFloat() < 1.0f / (float)arg.chance) {
            return Stream.of(arg2);
        }
        return Stream.empty();
    }
}

