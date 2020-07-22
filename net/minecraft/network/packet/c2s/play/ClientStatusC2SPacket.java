/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ClientStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Mode mode;

    public ClientStatusC2SPacket() {
    }

    public ClientStatusC2SPacket(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.mode = buf.readEnumConstant(Mode.class);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.mode);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onClientStatus(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public static enum Mode {
        PERFORM_RESPAWN,
        REQUEST_STATS;

    }
}

