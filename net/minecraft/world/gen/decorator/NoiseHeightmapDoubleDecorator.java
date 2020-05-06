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
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;

public class NoiseHeightmapDoubleDecorator
extends Decorator<NoiseHeightmapDecoratorConfig> {
    public NoiseHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends NoiseHeightmapDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld arg, ChunkGenerator<? extends ChunkGeneratorConfig> arg2, Random random, NoiseHeightmapDecoratorConfig arg3, BlockPos arg4) {
        double d = Biome.FOLIAGE_NOISE.sample((double)arg4.getX() / 200.0, (double)arg4.getZ() / 200.0, false);
        int i2 = d < arg3.noiseLevel ? arg3.belowNoise : arg3.aboveNoise;
        return IntStream.range(0, i2).mapToObj(i -> {
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

