/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class MobSpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private UUID uuid;
    private int entityTypeId;
    private double x;
    private double y;
    private double z;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private byte yaw;
    private byte pitch;
    private byte headYaw;

    public MobSpawnS2CPacket() {
    }

    public MobSpawnS2CPacket(LivingEntity arg) {
        this.id = arg.getEntityId();
        this.uuid = arg.getUuid();
        this.entityTypeId = Registry.ENTITY_TYPE.getRawId(arg.getType());
        this.x = arg.getX();
        this.y = arg.getY();
        this.z = arg.getZ();
        this.yaw = (byte)(arg.yaw * 256.0f / 360.0f);
        this.pitch = (byte)(arg.pitch * 256.0f / 360.0f);
        this.headYaw = (byte)(arg.headYaw * 256.0f / 360.0f);
        double d = 3.9;
        Vec3d lv = arg.getVelocity();
        double e = MathHelper.clamp(lv.x, -3.9, 3.9);
        double f = MathHelper.clamp(lv.y, -3.9, 3.9);
        double g = MathHelper.clamp(lv.z, -3.9, 3.9);
        this.velocityX = (int)(e * 8000.0);
        this.velocityY = (int)(f * 8000.0);
        this.velocityZ = (int)(g * 8000.0);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.uuid = arg.readUuid();
        this.entityTypeId = arg.readVarInt();
        this.x = arg.readDouble();
        this.y = arg.readDouble();
        this.z = arg.readDouble();
        this.yaw = arg.readByte();
        this.pitch = arg.readByte();
        this.headYaw = arg.readByte();
        this.velocityX = arg.readShort();
        this.velocityY = arg.readShort();
        this.velocityZ = arg.readShort();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeUuid(this.uuid);
        arg.writeVarInt(this.entityTypeId);
        arg.writeDouble(this.x);
        arg.writeDouble(this.y);
        arg.writeDouble(this.z);
        arg.writeByte(this.yaw);
        arg.writeByte(this.pitch);
        arg.writeByte(this.headYaw);
        arg.writeShort(this.velocityX);
        arg.writeShort(this.velocityY);
        arg.writeShort(this.velocityZ);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onMobSpawn(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public UUID getUuid() {
        return this.uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityTypeId() {
        return this.entityTypeId;
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
    public int getVelocityX() {
        return this.velocityX;
    }

    @Environment(value=EnvType.CLIENT)
    public int getVelocityY() {
        return this.velocityY;
    }

    @Environment(value=EnvType.CLIENT)
    public int getVelocityZ() {
        return this.velocityZ;
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
    public byte getHeadYaw() {
        return this.headYaw;
    }
}

