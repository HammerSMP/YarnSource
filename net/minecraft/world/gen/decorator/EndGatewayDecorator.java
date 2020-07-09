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
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class EndGatewayDecorator
extends Decorator<NopeDecoratorConfig> {
    public EndGatewayDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, NopeDecoratorConfig arg2, BlockPos arg3) {
        int j;
        int i;
        int k;
        if (random.nextInt(700) == 0 && (k = arg.method_30460(Heightmap.Type.MOTION_BLOCKING, i = random.nextInt(16) + arg3.getX(), j = random.nextInt(16) + arg3.getZ())) > 0) {
            int l = k + 3 + random.nextInt(7);
            return Stream.of(new BlockPos(i, l, j));
        }
        return Stream.empty();
    }
}

