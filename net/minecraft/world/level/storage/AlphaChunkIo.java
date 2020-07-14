/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.level.storage;

import net.minecraft.class_5455;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.level.storage.AlphaChunkDataArray;

public class AlphaChunkIo {
    public static AlphaChunk readAlphaChunk(CompoundTag tag) {
        int i = tag.getInt("xPos");
        int j = tag.getInt("zPos");
        AlphaChunk lv = new AlphaChunk(i, j);
        lv.blocks = tag.getByteArray("Blocks");
        lv.data = new AlphaChunkDataArray(tag.getByteArray("Data"), 7);
        lv.skyLight = new AlphaChunkDataArray(tag.getByteArray("SkyLight"), 7);
        lv.blockLight = new AlphaChunkDataArray(tag.getByteArray("BlockLight"), 7);
        lv.heightMap = tag.getByteArray("HeightMap");
        lv.terrainPopulated = tag.getBoolean("TerrainPopulated");
        lv.entities = tag.getList("Entities", 10);
        lv.blockEntities = tag.getList("TileEntities", 10);
        lv.blockTicks = tag.getList("TileTicks", 10);
        try {
            lv.lastUpdate = tag.getLong("LastUpdate");
        }
        catch (ClassCastException classCastException) {
            lv.lastUpdate = tag.getInt("LastUpdate");
        }
        return lv;
    }

    public static void convertAlphaChunk(class_5455.class_5457 arg, AlphaChunk arg2, CompoundTag arg3, BiomeSource arg4) {
        arg3.putInt("xPos", arg2.x);
        arg3.putInt("zPos", arg2.z);
        arg3.putLong("LastUpdate", arg2.lastUpdate);
        int[] is = new int[arg2.heightMap.length];
        for (int i = 0; i < arg2.heightMap.length; ++i) {
            is[i] = arg2.heightMap[i];
        }
        arg3.putIntArray("HeightMap", is);
        arg3.putBoolean("TerrainPopulated", arg2.terrainPopulated);
        ListTag lv = new ListTag();
        for (int j = 0; j < 8; ++j) {
            boolean bl = true;
            for (int k = 0; k < 16 && bl; ++k) {
                block3: for (int l = 0; l < 16 && bl; ++l) {
                    for (int m = 0; m < 16; ++m) {
                        int n = k << 11 | m << 7 | l + (j << 4);
                        byte o = arg2.blocks[n];
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
                        byte t = arg2.blocks[s];
                        bs[q << 8 | r << 4 | p] = (byte)(t & 0xFF);
                        lv2.set(p, q, r, arg2.data.get(p, q + (j << 4), r));
                        lv3.set(p, q, r, arg2.skyLight.get(p, q + (j << 4), r));
                        lv4.set(p, q, r, arg2.blockLight.get(p, q + (j << 4), r));
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
        arg3.put("Sections", lv);
        arg3.putIntArray("Biomes", new BiomeArray(arg.method_30530(Registry.BIOME_KEY), new ChunkPos(arg2.x, arg2.z), arg4).toIntArray());
        arg3.put("Entities", arg2.entities);
        arg3.put("TileEntities", arg2.blockEntities);
        if (arg2.blockTicks != null) {
            arg3.put("TileTicks", arg2.blockTicks);
        }
        arg3.putBoolean("convertedFromAlphaFormat", true);
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

        public AlphaChunk(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }
}

