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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class ChorusPlantDecorator
extends Decorator<NopeDecoratorConfig> {
    public ChorusPlantDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld arg, ChunkGenerator<? extends ChunkGeneratorConfig> arg2, Random random, NopeDecoratorConfig arg3, BlockPos arg4) {
        int i2 = random.nextInt(5);
        return IntStream.range(0, i2).mapToObj(i -> {
            int k;
            int j = random.nextInt(16) + arg4.getX();
            int l = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k = random.nextInt(16) + arg4.getZ());
            if (l > 0) {
                int m = l - 1;
                return new BlockPos(j, m, k);
            }
            return null;
        }).filter(Objects::nonNull);
    }
}

