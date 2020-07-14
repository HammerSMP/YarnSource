/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PlayerPositionLookS2CPacket
implements Packet<ClientPlayPacketListener> {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Set<Flag> flags;
    private int teleportId;

    public PlayerPositionLookS2CPacket() {
    }

    public PlayerPositionLookS2CPacket(double x, double y, double z, float yaw, float pitch, Set<Flag> flags, int teleportId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
        this.teleportId = teleportId;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.flags = Flag.getFlags(buf.readUnsignedByte());
        this.teleportId = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(Flag.getBitfield(this.flags));
        buf.writeVarInt(this.teleportId);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerPositionLook(this);
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

    @Environment(value=EnvType.CLIENT)
    public int getTeleportId() {
        return this.teleportId;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<Flag> getFlags() {
        return this.flags;
    }

    public static enum Flag {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int shift;

        private Flag(int shift) {
            this.shift = shift;
        }

        private int getMask() {
            return 1 << this.shift;
        }

        private boolean isSet(int i) {
            return (i & this.getMask()) == this.getMask();
        }

        public static Set<Flag> getFlags(int i) {
            EnumSet<Flag> set = EnumSet.noneOf(Flag.class);
            for (Flag lv : Flag.values()) {
                if (!lv.isSet(i)) continue;
                set.add(lv);
            }
            return set;
        }

        public static int getBitfield(Set<Flag> set) {
            int i = 0;
            for (Flag lv : set) {
                i |= lv.getMask();
            }
            return i;
        }
    }
}

