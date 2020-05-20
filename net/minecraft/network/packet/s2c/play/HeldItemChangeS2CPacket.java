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

public class HeldItemChangeS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int slot;

    public HeldItemChangeS2CPacket() {
    }

    public HeldItemChangeS2CPacket(int i) {
        this.slot = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.slot = arg.readByte();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.slot);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onHeldItemChange(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }
}
