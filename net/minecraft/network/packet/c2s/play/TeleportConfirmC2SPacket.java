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

public class TeleportConfirmC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int teleportId;

    public TeleportConfirmC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public TeleportConfirmC2SPacket(int i) {
        this.teleportId = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.teleportId = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.teleportId);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onTeleportConfirm(this);
    }

    public int getTeleportId() {
        return this.teleportId;
    }
}

