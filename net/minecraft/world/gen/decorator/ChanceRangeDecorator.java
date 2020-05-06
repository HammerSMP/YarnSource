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
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class ChanceRangeDecorator
extends SimpleDecorator<ChanceRangeDecoratorConfig> {
    public ChanceRangeDecorator(Function<Dynamic<?>, ? extends ChanceRangeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, ChanceRangeDecoratorConfig arg, BlockPos arg2) {
        if (random.nextFloat() < arg.chance) {
            int i = random.nextInt(16) + arg2.getX();
            int j = random.nextInt(16) + arg2.getZ();
            int k = random.nextInt(arg.top - arg.topOffset) + arg.bottomOffset;
            return Stream.of(new BlockPos(i, k, j));
        }
        return Stream.empty();
    }
}

