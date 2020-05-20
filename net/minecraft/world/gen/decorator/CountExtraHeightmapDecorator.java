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
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CountExtraHeightmapDecorator
extends Decorator<CountExtraChanceDecoratorConfig> {
    public CountExtraHeightmapDecorator(Codec<CountExtraChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, CountExtraChanceDecoratorConfig arg3, BlockPos arg4) {
        int i2 = arg3.count;
        if (random.nextFloat() < arg3.extraChance) {
            i2 += arg3.extraCount;
        }
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16) + arg4.getX();
            int k = random.nextInt(16) + arg4.getZ();
            int l = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k);
            return new BlockPos(j, l, k);
        });
    }
}

