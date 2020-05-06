/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;

public class SpectatorTeleportC2SPacket
implements Packet<ServerPlayPacketListener> {
    private UUID targetUuid;

    public SpectatorTeleportC2SPacket() {
    }

    public SpectatorTeleportC2SPacket(UUID uUID) {
        this.targetUuid = uUID;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.targetUuid = arg.readUuid();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeUuid(this.targetUuid);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onSpectatorTeleport(this);
    }

    @Nullable
    public Entity getTarget(ServerWorld arg) {
        return arg.getEntity(this.targetUuid);
    }
}

