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
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class WaterLakeDecorator
extends Decorator<ChanceDecoratorConfig> {
    public WaterLakeDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld arg, ChunkGenerator<? extends ChunkGeneratorConfig> arg2, Random random, ChanceDecoratorConfig arg3, BlockPos arg4) {
        if (random.nextInt(arg3.chance) == 0) {
            int i = random.nextInt(16) + arg4.getX();
            int j = random.nextInt(16) + arg4.getZ();
            int k = random.nextInt(arg2.getMaxY());
            return Stream.of(new BlockPos(i, k, j));
        }
        return Stream.empty();
    }
}

