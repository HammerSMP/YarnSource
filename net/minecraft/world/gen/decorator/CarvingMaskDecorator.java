/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.class_5444;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CarvingMaskDecorator
extends Decorator<CarvingMaskDecoratorConfig> {
    public CarvingMaskDecorator(Codec<CarvingMaskDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, CarvingMaskDecoratorConfig arg2, BlockPos arg3) {
        ChunkPos lv = new ChunkPos(arg3);
        BitSet bitSet = arg.method_30459(lv, arg2.step);
        return IntStream.range(0, bitSet.length()).filter(i -> bitSet.get(i) && random.nextFloat() < arg.probability).mapToObj(i -> {
            int j = i & 0xF;
            int k = i >> 4 & 0xF;
            int l = i >> 8;
            return new BlockPos(lv.getStartX() + j, l, lv.getStartZ() + k);
        });
    }
}

