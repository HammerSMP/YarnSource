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

    public ChunkRenderDistanceCenterS2CPacket(int i, int j) {
        this.chunkX = i;
        this.chunkZ = j;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.chunkX = arg.readVarInt();
        this.chunkZ = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.chunkX);
        arg.writeVarInt(this.chunkZ);
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

