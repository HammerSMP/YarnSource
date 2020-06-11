/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class UpdatePlayerAbilitiesC2SPacket
implements Packet<ServerPlayPacketListener> {
    private boolean flying;

    public UpdatePlayerAbilitiesC2SPacket() {
    }

    public UpdatePlayerAbilitiesC2SPacket(PlayerAbilities arg) {
        this.flying = arg.flying;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        byte b = arg.readByte();
        this.flying = (b & 2) != 0;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        int b = 0;
        if (this.flying) {
            b = (byte)(b | 2);
        }
        arg.writeByte(b);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onPlayerAbilities(this);
    }

    public boolean isFlying() {
        return this.flying;
    }
}

