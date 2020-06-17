/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;

public class SetCameraEntityS2CPacket
implements Packet<ClientPlayPacketListener> {
    public int entityId;

    public SetCameraEntityS2CPacket() {
    }

    public SetCameraEntityS2CPacket(Entity arg) {
        this.entityId = arg.getEntityId();
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityId = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityId);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSetCameraEntity(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World arg) {
        return arg.getEntityById(this.entityId);
    }
}

