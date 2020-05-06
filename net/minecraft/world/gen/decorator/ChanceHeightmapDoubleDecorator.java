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
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class ChanceHeightmapDoubleDecorator
extends Decorator<ChanceDecoratorConfig> {
    public ChanceHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld arg, ChunkGenerator<? extends ChunkGeneratorConfig> arg2, Random random, ChanceDecoratorConfig arg3, BlockPos arg4) {
        if (random.nextFloat() < 1.0f / (float)arg3.chance) {
            int j;
            int i = random.nextInt(16) + arg4.getX();
            int k = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, i, j = random.nextInt(16) + arg4.getZ()) * 2;
            if (k <= 0) {
                return Stream.empty();
            }
            return Stream.of(new BlockPos(i, random.nextInt(k), j));
        }
        return Stream.empty();
    }
}

