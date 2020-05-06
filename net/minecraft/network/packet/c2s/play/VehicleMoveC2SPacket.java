/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class VehicleMoveC2SPacket
implements Packet<ServerPlayPacketListener> {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public VehicleMoveC2SPacket() {
    }

    public VehicleMoveC2SPacket(Entity arg) {
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
    public void apply(ServerPlayPacketListener arg) {
        arg.onVehicleMove(this);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}

