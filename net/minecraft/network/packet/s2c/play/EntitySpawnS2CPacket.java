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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class EntitySpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private int pitch;
    private int yaw;
    private EntityType<?> entityTypeId;
    private int entityData;

    public EntitySpawnS2CPacket() {
    }

    public EntitySpawnS2CPacket(int i, UUID uUID, double d, double e, double f, float g, float h, EntityType<?> arg, int j, Vec3d arg2) {
        this.id = i;
        this.uuid = uUID;
        this.x = d;
        this.y = e;
        this.z = f;
        this.pitch = MathHelper.floor(g * 256.0f / 360.0f);
        this.yaw = MathHelper.floor(h * 256.0f / 360.0f);
        this.entityTypeId = arg;
        this.entityData = j;
        this.velocityX = (int)(MathHelper.clamp(arg2.x, -3.9, 3.9) * 8000.0);
        this.velocityY = (int)(MathHelper.clamp(arg2.y, -3.9, 3.9) * 8000.0);
        this.velocityZ = (int)(MathHelper.clamp(arg2.z, -3.9, 3.9) * 8000.0);
    }

    public EntitySpawnS2CPacket(Entity arg) {
        this(arg, 0);
    }

    public EntitySpawnS2CPacket(Entity arg, int i) {
        this(arg.getEntityId(), arg.getUuid(), arg.getX(), arg.getY(), arg.getZ(), arg.pitch, arg.yaw, arg.getType(), i, arg.getVelocity());
    }

    public EntitySpawnS2CPacket(Entity arg, EntityType<?> arg2, int i, BlockPos arg3) {
        this(arg.getEntityId(), arg.getUuid(), arg3.getX(), arg3.getY(), arg3.getZ(), arg.pitch, arg.yaw, arg2, i, arg.getVelocity());
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.uuid = arg.readUuid();
        this.entityTypeId = Registry.ENTITY_TYPE.get(arg.readVarInt());
        this.x = arg.readDouble();
        this.y = arg.readDouble();
        this.z = arg.readDouble();
        this.pitch = arg.readByte();
        this.yaw = arg.readByte();
        this.entityData = arg.readInt();
        this.velocityX = arg.readShort();
        this.velocityY = arg.readShort();
        this.velocityZ = arg.readShort();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeUuid(this.uuid);
        arg.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.entityTypeId));
        arg.writeDouble(this.x);
        arg.writeDouble(this.y);
        arg.writeDouble(this.z);
        arg.writeByte(this.pitch);
        arg.writeByte(this.yaw);
        arg.writeInt(this.entityData);
        arg.writeShort(this.velocityX);
        arg.writeShort(this.velocityY);
        arg.writeShort(this.velocityZ);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntitySpawn(this);
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
    public double getVelocityX() {
        return (double)this.velocityX / 8000.0;
    }

    @Environment(value=EnvType.CLIENT)
    public double getVelocityY() {
        return (double)this.velocityY / 8000.0;
    }

    @Environment(value=EnvType.CLIENT)
    public double getVelocityz() {
        return (double)this.velocityZ / 8000.0;
    }

    @Environment(value=EnvType.CLIENT)
    public int getPitch() {
        return this.pitch;
    }

    @Environment(value=EnvType.CLIENT)
    public int getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public EntityType<?> getEntityTypeId() {
        return this.entityTypeId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityData() {
        return this.entityData;
    }
}

