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
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CountHeight64Decorator
extends Decorator<CountDecoratorConfig> {
    public CountHeight64Decorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, CountDecoratorConfig arg3, BlockPos arg4) {
        return IntStream.range(0, arg3.count).mapToObj(i -> {
            int j = random.nextInt(16) + arg4.getX();
            int k = random.nextInt(16) + arg4.getZ();
            int l = 64;
            return new BlockPos(j, 64, k);
        });
    }
}

