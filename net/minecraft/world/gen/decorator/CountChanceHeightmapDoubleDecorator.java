/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CountChanceHeightmapDoubleDecorator
extends Decorator<CountChanceDecoratorConfig> {
    public CountChanceHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends CountChanceDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, CountChanceDecoratorConfig arg3, BlockPos arg4) {
        return IntStream.range(0, arg3.count).filter(i -> random.nextFloat() < arg.chance).mapToObj(i -> {
            int k;
            int j = random.nextInt(16) + arg4.getX();
            int l = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k = random.nextInt(16) + arg4.getZ()) * 2;
            if (l <= 0) {
                return null;
            }
            return new BlockPos(j, random.nextInt(l), k);
        }).filter(Objects::nonNull);
    }
}

