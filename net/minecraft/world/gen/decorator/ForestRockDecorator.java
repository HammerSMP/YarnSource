/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class ForestRockDecorator
extends Decorator<CountDecoratorConfig> {
    public ForestRockDecorator(Codec<CountDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, CountDecoratorConfig arg3, BlockPos arg4) {
        int i2 = random.nextInt(arg3.count);
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16) + arg4.getX();
            int k = random.nextInt(16) + arg4.getZ();
            int l = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k);
            return new BlockPos(j, l, k);
        });
    }
}

