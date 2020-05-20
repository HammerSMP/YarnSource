/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CountHeightmapDoubleDecorator
extends Decorator<CountDecoratorConfig> {
    public CountHeightmapDoubleDecorator(Codec<CountDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, CountDecoratorConfig arg3, BlockPos arg4) {
        return IntStream.range(0, arg3.count).mapToObj(i -> {
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

