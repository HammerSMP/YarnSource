/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.s2c.query;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;

public class QueryPongS2CPacket
implements Packet<ClientQueryPacketListener> {
    private long startTime;

    public QueryPongS2CPacket() {
    }

    public QueryPongS2CPacket(long l) {
        this.startTime = l;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.startTime = arg.readLong();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeLong(this.startTime);
    }

    @Override
    public void apply(ClientQueryPacketListener arg) {
        arg.onPong(this);
    }
}

