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
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LookAtS2CPacket
implements Packet<ClientPlayPacketListener> {
    private double targetX;
    private double targetY;
    private double targetZ;
    private int entityId;
    private EntityAnchorArgumentType.EntityAnchor selfAnchor;
    private EntityAnchorArgumentType.EntityAnchor targetAnchor;
    private boolean lookAtEntity;

    public LookAtS2CPacket() {
    }

    public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor arg, double targetX, double targetY, double targetZ) {
        this.selfAnchor = arg;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor selfAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
        this.selfAnchor = selfAnchor;
        this.entityId = entity.getEntityId();
        this.targetAnchor = targetAnchor;
        Vec3d lv = targetAnchor.positionAt(entity);
        this.targetX = lv.x;
        this.targetY = lv.y;
        this.targetZ = lv.z;
        this.lookAtEntity = true;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.selfAnchor = buf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        this.targetX = buf.readDouble();
        this.targetY = buf.readDouble();
        this.targetZ = buf.readDouble();
        if (buf.readBoolean()) {
            this.lookAtEntity = true;
            this.entityId = buf.readVarInt();
            this.targetAnchor = buf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.selfAnchor);
        buf.writeDouble(this.targetX);
        buf.writeDouble(this.targetY);
        buf.writeDouble(this.targetZ);
        buf.writeBoolean(this.lookAtEntity);
        if (this.lookAtEntity) {
            buf.writeVarInt(this.entityId);
            buf.writeEnumConstant(this.targetAnchor);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onLookAt(this);
    }

    @Environment(value=EnvType.CLIENT)
    public EntityAnchorArgumentType.EntityAnchor getSelfAnchor() {
        return this.selfAnchor;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Vec3d getTargetPosition(World world) {
        if (this.lookAtEntity) {
            Entity lv = world.getEntityById(this.entityId);
            if (lv == null) {
                return new Vec3d(this.targetX, this.targetY, this.targetZ);
            }
            return this.targetAnchor.positionAt(lv);
        }
        return new Vec3d(this.targetX, this.targetY, this.targetZ);
    }
}

