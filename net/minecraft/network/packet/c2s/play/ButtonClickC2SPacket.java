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

public class ButtonClickC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int syncId;
    private int buttonId;

    public ButtonClickC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public ButtonClickC2SPacket(int syncId, int buttonId) {
        this.syncId = syncId;
        this.buttonId = buttonId;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onButtonClick(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.syncId = buf.readByte();
        this.buttonId = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
        buf.writeByte(this.buttonId);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getButtonId() {
        return this.buttonId;
    }
}

