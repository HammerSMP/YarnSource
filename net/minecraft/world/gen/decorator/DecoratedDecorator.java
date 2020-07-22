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
import net.minecraft.world.gen.decorator.DecoratedDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class DecoratedDecorator
extends Decorator<DecoratedDecoratorConfig> {
    public DecoratedDecorator(Codec<DecoratedDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext arg, Random random, DecoratedDecoratorConfig arg2, BlockPos arg32) {
        return arg2.method_30455().method_30444(arg, random, arg32).flatMap(arg3 -> arg2.method_30457().method_30444(arg, random, (BlockPos)arg3));
    }
}

