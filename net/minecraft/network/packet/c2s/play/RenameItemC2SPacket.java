/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class RenameItemC2SPacket
implements Packet<ServerPlayPacketListener> {
    private String name;

    public RenameItemC2SPacket() {
    }

    public RenameItemC2SPacket(String string) {
        this.name = string;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.name = arg.readString(32767);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(this.name);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onRenameItem(this);
    }

    public String getName() {
        return this.name;
    }
}

