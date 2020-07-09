/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class class_5449
extends SimpleDecorator<RangeDecoratorConfig> {
    public class_5449(Codec<RangeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, RangeDecoratorConfig arg, BlockPos arg2) {
        int i = arg2.getX();
        int j = arg2.getZ();
        int k = random.nextInt(arg.maximum - arg.topOffset) + arg.bottomOffset;
        return Stream.of(new BlockPos(i, k, j));
    }
}

