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

public class ScreenHandlerPropertyUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private int propertyId;
    private int value;

    public ScreenHandlerPropertyUpdateS2CPacket() {
    }

    public ScreenHandlerPropertyUpdateS2CPacket(int i, int j, int k) {
        this.syncId = i;
        this.propertyId = j;
        this.value = k;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onScreenHandlerPropertyUpdate(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readUnsignedByte();
        this.propertyId = arg.readShort();
        this.value = arg.readShort();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
        arg.writeShort(this.propertyId);
        arg.writeShort(this.value);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getPropertyId() {
        return this.propertyId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getValue() {
        return this.value;
    }
}

