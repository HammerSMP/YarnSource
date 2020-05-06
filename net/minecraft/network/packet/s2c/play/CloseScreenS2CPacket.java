/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class CloseScreenS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;

    public CloseScreenS2CPacket() {
    }

    public CloseScreenS2CPacket(int i) {
        this.syncId = i;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onCloseScreen(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readUnsignedByte();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
    }
}

