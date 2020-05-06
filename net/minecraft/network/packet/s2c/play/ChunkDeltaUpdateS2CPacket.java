/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkDeltaUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private ChunkPos chunkPos;
    private ChunkDeltaRecord[] records;

    public ChunkDeltaUpdateS2CPacket() {
    }

    public ChunkDeltaUpdateS2CPacket(int i, short[] ss, WorldChunk arg) {
        this.chunkPos = arg.getPos();
        this.records = new ChunkDeltaRecord[i];
        for (int j = 0; j < this.records.length; ++j) {
            this.records[j] = new ChunkDeltaRecord(ss[j], arg);
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.chunkPos = new ChunkPos(arg.readInt(), arg.readInt());
        this.records = new ChunkDeltaRecord[arg.readVarInt()];
        for (int i = 0; i < this.records.length; ++i) {
            this.records[i] = new ChunkDeltaRecord(arg.readShort(), Block.STATE_IDS.get(arg.readVarInt()));
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(this.chunkPos.x);
        arg.writeInt(this.chunkPos.z);
        arg.writeVarInt(this.records.length);
        for (ChunkDeltaRecord lv : this.records) {
            arg.writeShort(lv.getPosShort());
            arg.writeVarInt(Block.getRawIdFromState(lv.getState()));
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onChunkDeltaUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public ChunkDeltaRecord[] getRecords() {
        return this.records;
    }

    public class ChunkDeltaRecord {
        private final short pos;
        private final BlockState state;

        public ChunkDeltaRecord(short s, BlockState arg2) {
            this.pos = s;
            this.state = arg2;
        }

        public ChunkDeltaRecord(short s, WorldChunk arg2) {
            this.pos = s;
            this.state = arg2.getBlockState(this.getBlockPos());
        }

        public BlockPos getBlockPos() {
            return new BlockPos(ChunkDeltaUpdateS2CPacket.this.chunkPos.toBlockPos(this.pos >> 12 & 0xF, this.pos & 0xFF, this.pos >> 8 & 0xF));
        }

        public short getPosShort() {
            return this.pos;
        }

        public BlockState getState() {
            return this.state;
        }
    }
}

