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
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class ChancePassthroughDecorator
extends SimpleDecorator<ChanceDecoratorConfig> {
    public ChancePassthroughDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, ChanceDecoratorConfig arg, BlockPos arg2) {
        if (random.nextFloat() < 1.0f / (float)arg.chance) {
            return Stream.of(arg2);
        }
        return Stream.empty();
    }
}

