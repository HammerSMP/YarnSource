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

public class EntityPositionS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    public EntityPositionS2CPacket() {
    }

    public EntityPositionS2CPacket(Entity arg) {
        this.id = arg.getEntityId();
        this.x = arg.getX();
        this.y = arg.getY();
        this.z = arg.getZ();
        this.yaw = (byte)(arg.yaw * 256.0f / 360.0f);
        this.pitch = (byte)(arg.pitch * 256.0f / 360.0f);
        this.onGround = arg.isOnGround();
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.x = arg.readDouble();
        this.y = arg.readDouble();
        this.z = arg.readDouble();
        this.yaw = arg.readByte();
        this.pitch = arg.readByte();
        this.onGround = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeDouble(this.x);
        arg.writeDouble(this.y);
        arg.writeDouble(this.z);
        arg.writeByte(this.yaw);
        arg.writeByte(this.pitch);
        arg.writeBoolean(this.onGround);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityPosition(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public double getX() {
        return this.x;
    }

    @Environment(value=EnvType.CLIENT)
    public double getY() {
        return this.y;
    }

    @Environment(value=EnvType.CLIENT)
    public double getZ() {
        return this.z;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getPitch() {
        return this.pitch;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isOnGround() {
        return this.onGround;
    }
}

