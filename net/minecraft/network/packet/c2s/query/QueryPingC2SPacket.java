/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.query;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerQueryPacketListener;

public class QueryPingC2SPacket
implements Packet<ServerQueryPacketListener> {
    private long startTime;

    public QueryPingC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public QueryPingC2SPacket(long l) {
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
    public void apply(ServerQueryPacketListener arg) {
        arg.onPing(this);
    }

    public long getStartTime() {
        return this.startTime;
    }
}

