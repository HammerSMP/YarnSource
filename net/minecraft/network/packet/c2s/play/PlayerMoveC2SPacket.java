/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class PlayerMoveC2SPacket
implements Packet<ServerPlayPacketListener> {
    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;
    protected boolean onGround;
    protected boolean changePosition;
    protected boolean changeLook;

    public PlayerMoveC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerMoveC2SPacket(boolean bl) {
        this.onGround = bl;
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onPlayerMove(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.onGround = arg.readUnsignedByte() != 0;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.onGround ? 1 : 0);
    }

    public double getX(double d) {
        return this.changePosition ? this.x : d;
    }

    public double getY(double d) {
        return this.changePosition ? this.y : d;
    }

    public double getZ(double d) {
        return this.changePosition ? this.z : d;
    }

    public float getYaw(float f) {
        return this.changeLook ? this.yaw : f;
    }

    public float getPitch(float f) {
        return this.changeLook ? this.pitch : f;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public static class LookOnly
    extends PlayerMoveC2SPacket {
        public LookOnly() {
            this.changeLook = true;
        }

        @Environment(value=EnvType.CLIENT)
        public LookOnly(float f, float g, boolean bl) {
            this.yaw = f;
            this.pitch = g;
            this.onGround = bl;
            this.changeLook = true;
        }

        @Override
        public void read(PacketByteBuf arg) throws IOException {
            this.yaw = arg.readFloat();
            this.pitch = arg.readFloat();
            super.read(arg);
        }

        @Override
        public void write(PacketByteBuf arg) throws IOException {
            arg.writeFloat(this.yaw);
            arg.writeFloat(this.pitch);
            super.write(arg);
        }
    }

    public static class PositionOnly
    extends PlayerMoveC2SPacket {
        public PositionOnly() {
            this.changePosition = true;
        }

        @Environment(value=EnvType.CLIENT)
        public PositionOnly(double d, double e, double f, boolean bl) {
            this.x = d;
            this.y = e;
            this.z = f;
            this.onGround = bl;
            this.changePosition = true;
        }

        @Override
        public void read(PacketByteBuf arg) throws IOException {
            this.x = arg.readDouble();
            this.y = arg.readDouble();
            this.z = arg.readDouble();
            super.read(arg);
        }

        @Override
        public void write(PacketByteBuf arg) throws IOException {
            arg.writeDouble(this.x);
            arg.writeDouble(this.y);
            arg.writeDouble(this.z);
            super.write(arg);
        }
    }

    public static class Both
    extends PlayerMoveC2SPacket {
        public Both() {
            this.changePosition = true;
            this.changeLook = true;
        }

        @Environment(value=EnvType.CLIENT)
        public Both(double d, double e, double f, float g, float h, boolean bl) {
            this.x = d;
            this.y = e;
            this.z = f;
            this.yaw = g;
            this.pitch = h;
            this.onGround = bl;
            this.changeLook = true;
            this.changePosition = true;
        }

        @Override
        public void read(PacketByteBuf arg) throws IOException {
            this.x = arg.readDouble();
            this.y = arg.readDouble();
            this.z = arg.readDouble();
            this.yaw = arg.readFloat();
            this.pitch = arg.readFloat();
            super.read(arg);
        }

        @Override
        public void write(PacketByteBuf arg) throws IOException {
            arg.writeDouble(this.x);
            arg.writeDouble(this.y);
            arg.writeDouble(this.z);
            arg.writeFloat(this.yaw);
            arg.writeFloat(this.pitch);
            super.write(arg);
        }
    }
}

