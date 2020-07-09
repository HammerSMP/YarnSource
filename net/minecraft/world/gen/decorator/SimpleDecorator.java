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
import net.minecraft.world.gen.decorator.DecoratorConfig;

public abstract class SimpleDecorator<DC extends DecoratorConfig>
extends Decorator<DC> {
    public SimpleDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    public final Stream<BlockPos> getPositions(class_5444 arg, Random random, DC arg2, BlockPos arg3) {
        return this.getPositions(random, arg2, arg3);
    }

    protected abstract Stream<BlockPos> getPositions(Random var1, DC var2, BlockPos var3);
}

