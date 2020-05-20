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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class WaterLakeDecorator
extends Decorator<ChanceDecoratorConfig> {
    public WaterLakeDecorator(Codec<ChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, ChanceDecoratorConfig arg3, BlockPos arg4) {
        if (random.nextInt(arg3.chance) == 0) {
            int i = random.nextInt(16) + arg4.getX();
            int j = random.nextInt(16) + arg4.getZ();
            int k = random.nextInt(arg2.getMaxY());
            return Stream.of(new BlockPos(i, k, j));
        }
        return Stream.empty();
    }
}

