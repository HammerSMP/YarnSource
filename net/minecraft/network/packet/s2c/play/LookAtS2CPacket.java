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
import net.minecraft.command.arguments.EntityAnchorArgumentType;
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

    public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor arg, double d, double e, double f) {
        this.selfAnchor = arg;
        this.targetX = d;
        this.targetY = e;
        this.targetZ = f;
    }

    public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor arg, Entity arg2, EntityAnchorArgumentType.EntityAnchor arg3) {
        this.selfAnchor = arg;
        this.entityId = arg2.getEntityId();
        this.targetAnchor = arg3;
        Vec3d lv = arg3.positionAt(arg2);
        this.targetX = lv.x;
        this.targetY = lv.y;
        this.targetZ = lv.z;
        this.lookAtEntity = true;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.selfAnchor = arg.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        this.targetX = arg.readDouble();
        this.targetY = arg.readDouble();
        this.targetZ = arg.readDouble();
        if (arg.readBoolean()) {
            this.lookAtEntity = true;
            this.entityId = arg.readVarInt();
            this.targetAnchor = arg.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.selfAnchor);
        arg.writeDouble(this.targetX);
        arg.writeDouble(this.targetY);
        arg.writeDouble(this.targetZ);
        arg.writeBoolean(this.lookAtEntity);
        if (this.lookAtEntity) {
            arg.writeVarInt(this.entityId);
            arg.writeEnumConstant(this.targetAnchor);
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
    public Vec3d getTargetPosition(World arg) {
        if (this.lookAtEntity) {
            Entity lv = arg.getEntityById(this.entityId);
            if (lv == null) {
                return new Vec3d(this.targetX, this.targetY, this.targetZ);
            }
            return this.targetAnchor.positionAt(lv);
        }
        return new Vec3d(this.targetX, this.targetY, this.targetZ);
    }
}

