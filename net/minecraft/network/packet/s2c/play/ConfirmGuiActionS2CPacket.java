/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ConfirmGuiActionS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private short actionId;
    private boolean accepted;

    public ConfirmGuiActionS2CPacket() {
    }

    public ConfirmGuiActionS2CPacket(int i, short s, boolean bl) {
        this.id = i;
        this.actionId = s;
        this.accepted = bl;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onGuiActionConfirm(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readUnsignedByte();
        this.actionId = arg.readShort();
        this.accepted = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.id);
        arg.writeShort(this.actionId);
        arg.writeBoolean(this.accepted);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public short getActionId() {
        return this.actionId;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean wasAccepted() {
        return this.accepted;
    }
}

