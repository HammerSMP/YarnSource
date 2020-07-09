/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import java.util.BitSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class class_5444 {
    private final ServerWorldAccess field_25857;
    private final ChunkGenerator field_25858;

    public class_5444(ServerWorldAccess arg, ChunkGenerator arg2) {
        this.field_25857 = arg;
        this.field_25858 = arg2;
    }

    public int method_30460(Heightmap.Type arg, int i, int j) {
        return this.field_25857.getTopY(arg, i, j);
    }

    public int method_30458() {
        return this.field_25858.getMaxY();
    }

    public int method_30462() {
        return this.field_25858.getSeaLevel();
    }

    public BitSet method_30459(ChunkPos arg, GenerationStep.Carver arg2) {
        return ((ProtoChunk)this.field_25857.getChunk(arg.x, arg.z)).getOrCreateCarvingMask(arg2);
    }

    public BlockState method_30461(BlockPos arg) {
        return this.field_25857.getBlockState(arg);
    }
}

