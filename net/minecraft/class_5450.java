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
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class class_5450
extends SimpleDecorator<NopeDecoratorConfig> {
    public class_5450(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, NopeDecoratorConfig arg, BlockPos arg2) {
        int i = random.nextInt(16) + arg2.getX();
        int j = random.nextInt(16) + arg2.getZ();
        int k = arg2.getY();
        return Stream.of(new BlockPos(i, k, j));
    }
}

