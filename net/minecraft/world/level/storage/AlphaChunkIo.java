/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.level.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.level.storage.AlphaChunkDataArray;

public class AlphaChunkIo {
    public static AlphaChunk readAlphaChunk(CompoundTag arg) {
        int i = arg.getInt("xPos");
        int j = arg.getInt("zPos");
        AlphaChunk lv = new AlphaChunk(i, j);
        lv.blocks = arg.getByteArray("Blocks");
        lv.data = new AlphaChunkDataArray(arg.getByteArray("Data"), 7);
        lv.skyLight = new AlphaChunkDataArray(arg.getByteArray("SkyLight"), 7);
        lv.blockLight = new AlphaChunkDataArray(arg.getByteArray("BlockLight"), 7);
        lv.heightMap = arg.getByteArray("HeightMap");
        lv.terrainPopulated = arg.getBoolean("TerrainPopulated");
        lv.entities = arg.getList("Entities", 10);
        lv.blockEntities = arg.getList("TileEntities", 10);
        lv.blockTicks = arg.getList("TileTicks", 10);
        try {
            lv.lastUpdate = arg.getLong("LastUpdate");
        }
        catch (ClassCastException classCastException) {
            lv.lastUpdate = arg.getInt("LastUpdate");
        }
        return lv;
    }

    public static void convertAlphaChunk(AlphaChunk arg, CompoundTag arg2, BiomeSource arg3) {
        arg2.putInt("xPos", arg.x);
        arg2.putInt("zPos", arg.z);
        arg2.putLong("LastUpdate", arg.lastUpdate);
        int[] is = new int[arg.heightMap.length];
        for (int i = 0; i < arg.heightMap.length; ++i) {
            is[i] = arg.heightMap[i];
        }
        arg2.putIntArray("HeightMap", is);
        arg2.putBoolean("TerrainPopulated", arg.terrainPopulated);
        ListTag lv = new ListTag();
        for (int j = 0; j < 8; ++j) {
            boolean bl = true;
            for (int k = 0; k < 16 && bl; ++k) {
                block3: for (int l = 0; l < 16 && bl; ++l) {
                    for (int m = 0; m < 16; ++m) {
                        int n = k << 11 | m << 7 | l + (j << 4);
                        byte o = arg.blocks[n];
                        if (o == 0) continue;
                        bl = false;
                        continue block3;
                    }
                }
            }
            if (bl) continue;
            byte[] bs = new byte[4096];
            ChunkNibbleArray lv2 = new ChunkNibbleArray();
            ChunkNibbleArray lv3 = new ChunkNibbleArray();
            ChunkNibbleArray lv4 = new ChunkNibbleArray();
            for (int p = 0; p < 16; ++p) {
                for (int q = 0; q < 16; ++q) {
                    for (int r = 0; r < 16; ++r) {
                        int s = p << 11 | r << 7 | q + (j << 4);
                        byte t = arg.blocks[s];
                        bs[q << 8 | r << 4 | p] = (byte)(t & 0xFF);
                        lv2.set(p, q, r, arg.data.get(p, q + (j << 4), r));
                        lv3.set(p, q, r, arg.skyLight.get(p, q + (j << 4), r));
                        lv4.set(p, q, r, arg.blockLight.get(p, q + (j << 4), r));
                    }
                }
            }
            CompoundTag lv5 = new CompoundTag();
            lv5.putByte("Y", (byte)(j & 0xFF));
            lv5.putByteArray("Blocks", bs);
            lv5.putByteArray("Data", lv2.asByteArray());
            lv5.putByteArray("SkyLight", lv3.asByteArray());
            lv5.putByteArray("BlockLight", lv4.asByteArray());
            lv.add(lv5);
        }
        arg2.put("Sections", lv);
        arg2.putIntArray("Biomes", new BiomeArray(new ChunkPos(arg.x, arg.z), arg3).toIntArray());
        arg2.put("Entities", arg.entities);
        arg2.put("TileEntities", arg.blockEntities);
        if (arg.blockTicks != null) {
            arg2.put("TileTicks", arg.blockTicks);
        }
        arg2.putBoolean("convertedFromAlphaFormat", true);
    }

    public static class AlphaChunk {
        public long lastUpdate;
        public boolean terrainPopulated;
        public byte[] heightMap;
        public AlphaChunkDataArray blockLight;
        public AlphaChunkDataArray skyLight;
        public AlphaChunkDataArray data;
        public byte[] blocks;
        public ListTag entities;
        public ListTag blockEntities;
        public ListTag blockTicks;
        public final int x;
        public final int z;

        public AlphaChunk(int i, int j) {
            this.x = i;
            this.z = j;
        }
    }
}

