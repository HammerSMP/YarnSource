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

    public PlayerPositionLookS2CPacket(double d, double e, double f, float g, float h, Set<Flag> set, int i) {
        this.x = d;
        this.y = e;
        this.z = f;
        this.yaw = g;
        this.pitch = h;
        this.flags = set;
        this.teleportId = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.x = arg.readDouble();
        this.y = arg.readDouble();
        this.z = arg.readDouble();
        this.yaw = arg.readFloat();
        this.pitch = arg.readFloat();
        this.flags = Flag.getFlags(arg.readUnsignedByte());
        this.teleportId = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeDouble(this.x);
        arg.writeDouble(this.y);
        arg.writeDouble(this.z);
        arg.writeFloat(this.yaw);
        arg.writeFloat(this.pitch);
        arg.writeByte(Flag.getBitfield(this.flags));
        arg.writeVarInt(this.teleportId);
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

        private Flag(int j) {
            this.shift = j;
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

