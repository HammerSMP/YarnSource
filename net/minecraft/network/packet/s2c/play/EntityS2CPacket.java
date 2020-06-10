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
import net.minecraft.world.World;

public class EntityS2CPacket
implements Packet<ClientPlayPacketListener> {
    protected int id;
    protected short deltaX;
    protected short deltaY;
    protected short deltaZ;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean rotate;
    protected boolean positionChanged;

    public static long encodePacketCoordinate(double d) {
        return MathHelper.lfloor(d * 4096.0);
    }

    public static Vec3d decodePacketCoordinates(long l, long m, long n) {
        return new Vec3d(l, m, n).multiply(2.44140625E-4);
    }

    public EntityS2CPacket() {
    }

    public EntityS2CPacket(int i) {
        this.id = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityUpdate(this);
    }

    public String toString() {
        return "Entity_" + super.toString();
    }

    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World arg) {
        return arg.getEntityById(this.id);
    }

    @Environment(value=EnvType.CLIENT)
    public short getDeltaXShort() {
        return this.deltaX;
    }

    @Environment(value=EnvType.CLIENT)
    public short getDeltaYShort() {
        return this.deltaY;
    }

    @Environment(value=EnvType.CLIENT)
    public short getDeltaZShort() {
        return this.deltaZ;
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
    public boolean hasRotation() {
        return this.rotate;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isPositionChanged() {
        return this.positionChanged;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isOnGround() {
        return this.onGround;
    }

    public static class Rotate
    extends EntityS2CPacket {
        public Rotate() {
            this.rotate = true;
        }

        public Rotate(int i, byte b, byte c, boolean bl) {
            super(i);
            this.yaw = b;
            this.pitch = c;
            this.rotate = true;
            this.onGround = bl;
        }

        @Override
        public void read(PacketByteBuf arg) throws IOException {
            super.read(arg);
            this.yaw = arg.readByte();
            this.pitch = arg.readByte();
            this.onGround = arg.readBoolean();
        }

        @Override
        public void write(PacketByteBuf arg) throws IOException {
            super.write(arg);
            arg.writeByte(this.yaw);
            arg.writeByte(this.pitch);
            arg.writeBoolean(this.onGround);
        }
    }

    public static class MoveRelative
    extends EntityS2CPacket {
        public MoveRelative() {
            this.positionChanged = true;
        }

        public MoveRelative(int i, short s, short t, short u, boolean bl) {
            super(i);
            this.deltaX = s;
            this.deltaY = t;
            this.deltaZ = u;
            this.onGround = bl;
            this.positionChanged = true;
        }

        @Override
        public void read(PacketByteBuf arg) throws IOException {
            super.read(arg);
            this.deltaX = arg.readShort();
            this.deltaY = arg.readShort();
            this.deltaZ = arg.readShort();
            this.onGround = arg.readBoolean();
        }

        @Override
        public void write(PacketByteBuf arg) throws IOException {
            super.write(arg);
            arg.writeShort(this.deltaX);
            arg.writeShort(this.deltaY);
            arg.writeShort(this.deltaZ);
            arg.writeBoolean(this.onGround);
        }
    }

    public static class RotateAndMoveRelative
    extends EntityS2CPacket {
        public RotateAndMoveRelative() {
            this.rotate = true;
            this.positionChanged = true;
        }

        public RotateAndMoveRelative(int i, short s, short t, short u, byte b, byte c, boolean bl) {
            super(i);
            this.deltaX = s;
            this.deltaY = t;
            this.deltaZ = u;
            this.yaw = b;
            this.pitch = c;
            this.onGround = bl;
            this.rotate = true;
            this.positionChanged = true;
        }

        @Override
        public void read(PacketByteBuf arg) throws IOException {
            super.read(arg);
            this.deltaX = arg.readShort();
            this.deltaY = arg.readShort();
            this.deltaZ = arg.readShort();
            this.yaw = arg.readByte();
            this.pitch = arg.readByte();
            this.onGround = arg.readBoolean();
        }

        @Override
        public void write(PacketByteBuf arg) throws IOException {
            super.write(arg);
            arg.writeShort(this.deltaX);
            arg.writeShort(this.deltaY);
            arg.writeShort(this.deltaZ);
            arg.writeByte(this.yaw);
            arg.writeByte(this.pitch);
            arg.writeBoolean(this.onGround);
        }
    }
}

