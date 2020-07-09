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
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class IcebergDecorator
extends SimpleDecorator<NopeDecoratorConfig> {
    public IcebergDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, NopeDecoratorConfig arg, BlockPos arg2) {
        int i = random.nextInt(8) + 4 + arg2.getX();
        int j = random.nextInt(8) + 4 + arg2.getZ();
        return Stream.of(new BlockPos(i, arg2.getY(), j));
    }
}

