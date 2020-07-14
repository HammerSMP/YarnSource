/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.LightStorage;

public class BlockLightStorage
extends LightStorage<Data> {
    protected BlockLightStorage(ChunkProvider chunkProvider) {
        super(LightType.BLOCK, chunkProvider, new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)new Long2ObjectOpenHashMap()));
    }

    @Override
    protected int getLight(long blockPos) {
        long m = ChunkSectionPos.fromBlockPos(blockPos);
        ChunkNibbleArray lv = this.getLightSection(m, false);
        if (lv == null) {
            return 0;
        }
        return lv.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    public static final class Data
    extends ChunkToNibbleArrayMap<Data> {
        public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap) {
            super(long2ObjectOpenHashMap);
        }

        @Override
        public Data copy() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arrays.clone());
        }

        @Override
        public /* synthetic */ ChunkToNibbleArrayMap copy() {
            return this.copy();
        }
    }
}

