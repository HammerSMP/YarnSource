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

public class ConfirmGuiActionC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int windowId;
    private short actionId;
    private boolean accepted;

    public ConfirmGuiActionC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public ConfirmGuiActionC2SPacket(int i, short s, boolean bl) {
        this.windowId = i;
        this.actionId = s;
        this.accepted = bl;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onConfirmTransaction(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.windowId = arg.readByte();
        this.actionId = arg.readShort();
        this.accepted = arg.readByte() != 0;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.windowId);
        arg.writeShort(this.actionId);
        arg.writeByte(this.accepted ? 1 : 0);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public short getSyncId() {
        return this.actionId;
    }
}

