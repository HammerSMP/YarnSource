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
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;

public class EntitySetHeadYawS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entity;
    private byte headYaw;

    public EntitySetHeadYawS2CPacket() {
    }

    public EntitySetHeadYawS2CPacket(Entity arg, byte b) {
        this.entity = arg.getEntityId();
        this.headYaw = b;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entity = arg.readVarInt();
        this.headYaw = arg.readByte();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entity);
        arg.writeByte(this.headYaw);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntitySetHeadYaw(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World arg) {
        return arg.getEntityById(this.entity);
    }

    @Environment(value=EnvType.CLIENT)
    public byte getHeadYaw() {
        return this.headYaw;
    }
}

