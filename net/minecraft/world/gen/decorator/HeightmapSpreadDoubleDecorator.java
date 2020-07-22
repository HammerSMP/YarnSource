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
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.AbstractHeightmapDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class HeightmapSpreadDoubleDecorator<DC extends DecoratorConfig>
extends AbstractHeightmapDecorator<DC> {
    public HeightmapSpreadDoubleDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type getHeightmapType(DC config) {
        return Heightmap.Type.MOTION_BLOCKING;
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getZ();
        int k = context.getTopY(this.getHeightmapType(config), i, j);
        if (k == 0) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(new BlockPos(i, random.nextInt(k * 2), j));
    }
}

