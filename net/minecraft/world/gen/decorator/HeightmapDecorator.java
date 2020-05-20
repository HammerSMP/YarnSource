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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class HeightmapDecorator
extends Decorator<NopeDecoratorConfig> {
    public HeightmapDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
        super(function);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, NopeDecoratorConfig arg3, BlockPos arg4) {
        int i = random.nextInt(16) + arg4.getX();
        int j = random.nextInt(16) + arg4.getZ();
        int k = arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, i, j);
        return Stream.of(new BlockPos(i, k, j));
    }
}

