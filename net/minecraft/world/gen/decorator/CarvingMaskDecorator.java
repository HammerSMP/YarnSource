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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CarvingMaskDecorator
extends Decorator<CarvingMaskDecoratorConfig> {
    public CarvingMaskDecorator(Codec<CarvingMaskDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess arg, ChunkGenerator arg2, Random random, CarvingMaskDecoratorConfig arg3, BlockPos arg4) {
        Chunk lv = arg.getChunk(arg4);
        ChunkPos lv2 = lv.getPos();
        BitSet bitSet = ((ProtoChunk)lv).getCarvingMask(arg3.step);
        if (bitSet == null) {
            return Stream.empty();
        }
        return IntStream.range(0, bitSet.length()).filter(i -> bitSet.get(i) && random.nextFloat() < arg.probability).mapToObj(i -> {
            int j = i & 0xF;
            int k = i >> 4 & 0xF;
            int l = i >> 8;
            return new BlockPos(lv2.getStartX() + j, l, lv2.getStartZ() + k);
        });
    }
}

