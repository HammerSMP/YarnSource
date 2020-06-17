/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkDataS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int chunkX;
    private int chunkZ;
    private int verticalStripBitmask;
    private CompoundTag heightmaps;
    @Nullable
    private BiomeArray biomeArray;
    private byte[] data;
    private List<CompoundTag> blockEntities;
    private boolean isFullChunk;
    private boolean field_25720;

    public ChunkDataS2CPacket() {
    }

    public ChunkDataS2CPacket(WorldChunk arg, int i, boolean bl) {
        ChunkPos lv = arg.getPos();
        this.chunkX = lv.x;
        this.chunkZ = lv.z;
        this.isFullChunk = i == 65535;
        this.field_25720 = bl;
        this.heightmaps = new CompoundTag();
        for (Map.Entry<Heightmap.Type, Heightmap> entry : arg.getHeightmaps()) {
            if (!entry.getKey().shouldSendToClient()) continue;
            this.heightmaps.put(entry.getKey().getName(), new LongArrayTag(entry.getValue().asLongArray()));
        }
        if (this.isFullChunk) {
            this.biomeArray = arg.getBiomeArray().copy();
        }
        this.data = new byte[this.getDataSize(arg, i)];
        this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), arg, i);
        this.blockEntities = Lists.newArrayList();
        for (Map.Entry<Object, Object> entry : arg.getBlockEntities().entrySet()) {
            BlockPos lv2 = (BlockPos)entry.getKey();
            BlockEntity lv3 = (BlockEntity)entry.getValue();
            int j = lv2.getY() >> 4;
            if (!this.isFullChunk() && (i & 1 << j) == 0) continue;
            CompoundTag lv4 = lv3.toInitialChunkDataTag();
            this.blockEntities.add(lv4);
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        int i;
        this.chunkX = arg.readInt();
        this.chunkZ = arg.readInt();
        this.isFullChunk = arg.readBoolean();
        this.field_25720 = arg.readBoolean();
        this.verticalStripBitmask = arg.readVarInt();
        this.heightmaps = arg.readCompoundTag();
        if (this.isFullChunk) {
            this.biomeArray = new BiomeArray(arg);
        }
        if ((i = arg.readVarInt()) > 0x200000) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        this.data = new byte[i];
        arg.readBytes(this.data);
        int j = arg.readVarInt();
        this.blockEntities = Lists.newArrayList();
        for (int k = 0; k < j; ++k) {
            this.blockEntities.add(arg.readCompoundTag());
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(this.chunkX);
        arg.writeInt(this.chunkZ);
        arg.writeBoolean(this.isFullChunk);
        arg.writeBoolean(this.field_25720);
        arg.writeVarInt(this.verticalStripBitmask);
        arg.writeCompoundTag(this.heightmaps);
        if (this.biomeArray != null) {
            this.biomeArray.toPacket(arg);
        }
        arg.writeVarInt(this.data.length);
        arg.writeBytes(this.data);
        arg.writeVarInt(this.blockEntities.size());
        for (CompoundTag lv : this.blockEntities) {
            arg.writeCompoundTag(lv);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onChunkData(this);
    }

    @Environment(value=EnvType.CLIENT)
    public PacketByteBuf getReadBuffer() {
        return new PacketByteBuf(Unpooled.wrappedBuffer((byte[])this.data));
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf byteBuf = Unpooled.wrappedBuffer((byte[])this.data);
        byteBuf.writerIndex(0);
        return byteBuf;
    }

    public int writeData(PacketByteBuf arg, WorldChunk arg2, int i) {
        int j = 0;
        ChunkSection[] lvs = arg2.getSectionArray();
        int l = lvs.length;
        for (int k = 0; k < l; ++k) {
            ChunkSection lv = lvs[k];
            if (lv == WorldChunk.EMPTY_SECTION || this.isFullChunk() && lv.isEmpty() || (i & 1 << k) == 0) continue;
            j |= 1 << k;
            lv.toPacket(arg);
        }
        return j;
    }

    protected int getDataSize(WorldChunk arg, int i) {
        int j = 0;
        ChunkSection[] lvs = arg.getSectionArray();
        int l = lvs.length;
        for (int k = 0; k < l; ++k) {
            ChunkSection lv = lvs[k];
            if (lv == WorldChunk.EMPTY_SECTION || this.isFullChunk() && lv.isEmpty() || (i & 1 << k) == 0) continue;
            j += lv.getPacketSize();
        }
        return j;
    }

    @Environment(value=EnvType.CLIENT)
    public int getX() {
        return this.chunkX;
    }

    @Environment(value=EnvType.CLIENT)
    public int getZ() {
        return this.chunkZ;
    }

    @Environment(value=EnvType.CLIENT)
    public int getVerticalStripBitmask() {
        return this.verticalStripBitmask;
    }

    public boolean isFullChunk() {
        return this.isFullChunk;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_30144() {
        return this.field_25720;
    }

    @Environment(value=EnvType.CLIENT)
    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }

    @Environment(value=EnvType.CLIENT)
    public List<CompoundTag> getBlockEntityTagList() {
        return this.blockEntities;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public BiomeArray getBiomeArray() {
        return this.biomeArray == null ? null : this.biomeArray.copy();
    }
}

