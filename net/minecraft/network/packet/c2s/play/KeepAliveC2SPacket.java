/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class KeepAliveC2SPacket
implements Packet<ServerPlayPacketListener> {
    private long id;

    public KeepAliveC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public KeepAliveC2SPacket(long id) {
        this.id = id;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onKeepAlive(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.id = buf.readLong();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}

