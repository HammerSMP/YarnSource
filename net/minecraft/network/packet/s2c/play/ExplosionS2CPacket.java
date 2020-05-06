/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ExplosionS2CPacket
implements Packet<ClientPlayPacketListener> {
    private double x;
    private double y;
    private double z;
    private float radius;
    private List<BlockPos> affectedBlocks;
    private float playerVelocityX;
    private float playerVelocityY;
    private float playerVelocityZ;

    public ExplosionS2CPacket() {
    }

    public ExplosionS2CPacket(double d, double e, double f, float g, List<BlockPos> list, Vec3d arg) {
        this.x = d;
        this.y = e;
        this.z = f;
        this.radius = g;
        this.affectedBlocks = Lists.newArrayList(list);
        if (arg != null) {
            this.playerVelocityX = (float)arg.x;
            this.playerVelocityY = (float)arg.y;
            this.playerVelocityZ = (float)arg.z;
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.x = arg.readFloat();
        this.y = arg.readFloat();
        this.z = arg.readFloat();
        this.radius = arg.readFloat();
        int i = arg.readInt();
        this.affectedBlocks = Lists.newArrayListWithCapacity((int)i);
        int j = MathHelper.floor(this.x);
        int k = MathHelper.floor(this.y);
        int l = MathHelper.floor(this.z);
        for (int m = 0; m < i; ++m) {
            int n = arg.readByte() + j;
            int o = arg.readByte() + k;
            int p = arg.readByte() + l;
            this.affectedBlocks.add(new BlockPos(n, o, p));
        }
        this.playerVelocityX = arg.readFloat();
        this.playerVelocityY = arg.readFloat();
        this.playerVelocityZ = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeFloat((float)this.x);
        arg.writeFloat((float)this.y);
        arg.writeFloat((float)this.z);
        arg.writeFloat(this.radius);
        arg.writeInt(this.affectedBlocks.size());
        int i = MathHelper.floor(this.x);
        int j = MathHelper.floor(this.y);
        int k = MathHelper.floor(this.z);
        for (BlockPos lv : this.affectedBlocks) {
            int l = lv.getX() - i;
            int m = lv.getY() - j;
            int n = lv.getZ() - k;
            arg.writeByte(l);
            arg.writeByte(m);
            arg.writeByte(n);
        }
        arg.writeFloat(this.playerVelocityX);
        arg.writeFloat(this.playerVelocityY);
        arg.writeFloat(this.playerVelocityZ);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onExplosion(this);
    }

    @Environment(value=EnvType.CLIENT)
    public float getPlayerVelocityX() {
        return this.playerVelocityX;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPlayerVelocityY() {
        return this.playerVelocityY;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPlayerVelocityZ() {
        return this.playerVelocityZ;
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
    public float getRadius() {
        return this.radius;
    }

    @Environment(value=EnvType.CLIENT)
    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }
}

