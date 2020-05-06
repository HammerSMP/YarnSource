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
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class MagmaDecorator
extends Decorator<CountDecoratorConfig> {
    public MagmaDecorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld arg, ChunkGenerator<? extends ChunkGeneratorConfig> arg2, Random random, CountDecoratorConfig arg3, BlockPos arg4) {
        int i = arg.getSeaLevel() / 2 + 1;
        return IntStream.range(0, arg3.count).mapToObj(j -> {
            int k = random.nextInt(16) + arg4.getX();
            int l = random.nextInt(16) + arg4.getZ();
            int m = i - 5 + random.nextInt(10);
            return new BlockPos(k, m, l);
        });
    }
}

