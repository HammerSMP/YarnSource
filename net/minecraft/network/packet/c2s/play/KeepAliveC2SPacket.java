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
    public KeepAliveC2SPacket(long l) {
        this.id = l;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onKeepAlive(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readLong();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}

