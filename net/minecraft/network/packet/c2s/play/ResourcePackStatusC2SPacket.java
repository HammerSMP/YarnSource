/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ResourcePackStatusC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Status status;

    public ResourcePackStatusC2SPacket() {
    }

    public ResourcePackStatusC2SPacket(Status arg) {
        this.status = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.status = arg.readEnumConstant(Status.class);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.status);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onResourcePackStatus(this);
    }

    public static enum Status {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;

    }
}

