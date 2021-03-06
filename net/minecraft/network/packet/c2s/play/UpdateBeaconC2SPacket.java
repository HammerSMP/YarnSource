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

public class UpdateBeaconC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int primaryEffectId;
    private int secondaryEffectId;

    public UpdateBeaconC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateBeaconC2SPacket(int primaryEffectId, int secondaryEffectId) {
        this.primaryEffectId = primaryEffectId;
        this.secondaryEffectId = secondaryEffectId;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.primaryEffectId = buf.readVarInt();
        this.secondaryEffectId = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.primaryEffectId);
        buf.writeVarInt(this.secondaryEffectId);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onUpdateBeacon(this);
    }

    public int getPrimaryEffectId() {
        return this.primaryEffectId;
    }

    public int getSecondaryEffectId() {
        return this.secondaryEffectId;
    }
}

