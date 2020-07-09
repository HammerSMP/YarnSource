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
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class LavaLakeDecorator
extends Decorator<ChanceDecoratorConfig> {
    public LavaLakeDecorator(Codec<ChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, ChanceDecoratorConfig arg2, BlockPos arg3) {
        if (random.nextInt(arg2.chance / 10) == 0) {
            int i = random.nextInt(16) + arg3.getX();
            int j = random.nextInt(16) + arg3.getZ();
            int k = random.nextInt(random.nextInt(arg.method_30458() - 8) + 8);
            if (k < arg.method_30462() || random.nextInt(arg2.chance / 8) == 0) {
                return Stream.of(new BlockPos(i, k, j));
            }
        }
        return Stream.empty();
    }
}

