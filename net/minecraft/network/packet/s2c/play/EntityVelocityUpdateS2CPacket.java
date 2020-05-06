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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityVelocityUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private int velocityX;
    private int velocityY;
    private int velocityZ;

    public EntityVelocityUpdateS2CPacket() {
    }

    public EntityVelocityUpdateS2CPacket(Entity arg) {
        this(arg.getEntityId(), arg.getVelocity());
    }

    public EntityVelocityUpdateS2CPacket(int i, Vec3d arg) {
        this.id = i;
        double d = 3.9;
        double e = MathHelper.clamp(arg.x, -3.9, 3.9);
        double f = MathHelper.clamp(arg.y, -3.9, 3.9);
        double g = MathHelper.clamp(arg.z, -3.9, 3.9);
        this.velocityX = (int)(e * 8000.0);
        this.velocityY = (int)(f * 8000.0);
        this.velocityZ = (int)(g * 8000.0);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.velocityX = arg.readShort();
        this.velocityY = arg.readShort();
        this.velocityZ = arg.readShort();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeShort(this.velocityX);
        arg.writeShort(this.velocityY);
        arg.writeShort(this.velocityZ);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onVelocityUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
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
}

