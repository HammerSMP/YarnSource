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
import net.minecraft.class_5444;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class MagmaDecorator
extends Decorator<NopeDecoratorConfig> {
    public MagmaDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, NopeDecoratorConfig arg2, BlockPos arg3) {
        int i = arg.method_30462();
        int j = i - 5 + random.nextInt(10);
        return Stream.of(new BlockPos(arg3.getX(), j, arg3.getZ()));
    }
}

