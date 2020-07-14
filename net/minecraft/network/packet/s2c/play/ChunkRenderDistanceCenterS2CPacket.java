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
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ChunkRenderDistanceCenterS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int chunkX;
    private int chunkZ;

    public ChunkRenderDistanceCenterS2CPacket() {
    }

    public ChunkRenderDistanceCenterS2CPacket(int x, int z) {
        this.chunkX = x;
        this.chunkZ = z;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.chunkX = buf.readVarInt();
        this.chunkZ = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.chunkX);
        buf.writeVarInt(this.chunkZ);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onChunkRenderDistanceCenter(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkX() {
        return this.chunkX;
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkZ() {
        return this.chunkZ;
    }
}

