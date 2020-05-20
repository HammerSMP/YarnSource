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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;

public class HeightmapNoiseBiasedDecorator
extends Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> {
    public HeightmapNoiseBiasedDecorator(Function<Dynamic<?>, ? extends TopSolidHeightmapNoiseBiasedDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, TopSolidHeightmapNoiseBiasedDecoratorConfig arg3, BlockPos arg4) {
        double d = Biome.FOLIAGE_NOISE.sample((double)arg4.getX() / arg3.noiseFactor, (double)arg4.getZ() / arg3.noiseFactor, false);
        int i2 = (int)Math.ceil((d + arg3.noiseOffset) * (double)arg3.noiseToCountRatio);
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16) + arg4.getX();
            int k = random.nextInt(16) + arg4.getZ();
            int l = arg.getTopY(arg3.heightmap, j, k);
            return new BlockPos(j, l, k);
        });
    }
}

