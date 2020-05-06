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

public class VehicleMoveS2CPacket
implements Packet<ClientPlayPacketListener> {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public VehicleMoveS2CPacket() {
    }

    public VehicleMoveS2CPacket(Entity arg) {
        this.x = arg.getX();
        this.y = arg.getY();
        this.z = arg.getZ();
        this.yaw = arg.yaw;
        this.pitch = arg.pitch;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.x = arg.readDouble();
        this.y = arg.readDouble();
        this.z = arg.readDouble();
        this.yaw = arg.readFloat();
        this.pitch = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeDouble(this.x);
        arg.writeDouble(this.y);
        arg.writeDouble(this.z);
        arg.writeFloat(this.yaw);
        arg.writeFloat(this.pitch);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onVehicleMove(this);
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
    public float getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPitch() {
        return this.pitch;
    }
}

