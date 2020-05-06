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

public class ChunkLoadDistanceS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int distance;

    public ChunkLoadDistanceS2CPacket() {
    }

    public ChunkLoadDistanceS2CPacket(int i) {
        this.distance = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.distance = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.distance);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onChunkLoadDistance(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getDistance() {
        return this.distance;
    }
}

