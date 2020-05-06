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

public class ChanceHeightmapDecorator
extends Decorator<ChanceDecoratorConfig> {
    public ChanceHeightmapDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld arg, ChunkGenerator<? extends ChunkGeneratorConfig> arg2, Random random, ChanceDecoratorConfig arg3, BlockPos arg4) {
        if (random.nextFloat() < 1.0f / (float)arg3.chance) {
            int i = random.nextInt(16) + arg4.getX();
            int j = random.nextInt(16) + arg4.getZ();
            int k = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, i, j);
            return Stream.of(new BlockPos(i, k, j));
        }
        return Stream.empty();
    }
}

