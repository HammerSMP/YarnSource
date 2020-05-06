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

public class OpenHorseScreenS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private int slotCount;
    private int horseId;

    public OpenHorseScreenS2CPacket() {
    }

    public OpenHorseScreenS2CPacket(int i, int j, int k) {
        this.syncId = i;
        this.slotCount = j;
        this.horseId = k;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onOpenHorseScreen(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readUnsignedByte();
        this.slotCount = arg.readVarInt();
        this.horseId = arg.readInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
        arg.writeVarInt(this.slotCount);
        arg.writeInt(this.horseId);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlotCount() {
        return this.slotCount;
    }

    @Environment(value=EnvType.CLIENT)
    public int getHorseId() {
        return this.horseId;
    }
}

