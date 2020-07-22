/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.query;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerQueryPacketListener;

public class QueryRequestC2SPacket
implements Packet<ServerQueryPacketListener> {
    @Override
    public void read(PacketByteBuf buf) throws IOException {
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
    }

    @Override
    public void apply(ServerQueryPacketListener arg) {
        arg.onRequest(this);
    }
}

