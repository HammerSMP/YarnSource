/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.class_5444;
import net.minecraft.class_5445;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class DarkOakTreeDecorator
extends class_5445<NopeDecoratorConfig> {
    public DarkOakTreeDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type method_30463(NopeDecoratorConfig arg) {
        return Heightmap.Type.MOTION_BLOCKING;
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, NopeDecoratorConfig arg2, BlockPos arg3) {
        return IntStream.range(0, 16).mapToObj(i -> {
            int j = i / 4;
            int k = i % 4;
            int l = j * 4 + 1 + random.nextInt(3) + arg3.getX();
            int m = k * 4 + 1 + random.nextInt(3) + arg3.getZ();
            int n = arg.method_30460(this.method_30463(arg2), l, m);
            return new BlockPos(l, n, m);
        });
    }
}

