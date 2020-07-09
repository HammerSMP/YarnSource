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
import net.minecraft.class_5444;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class ChorusPlantDecorator
extends Decorator<NopeDecoratorConfig> {
    public ChorusPlantDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, NopeDecoratorConfig arg2, BlockPos arg3) {
        int i = random.nextInt(arg3.getY() + 32);
        return Stream.of(new BlockPos(arg3.getX(), i, arg3.getZ()));
    }
}

