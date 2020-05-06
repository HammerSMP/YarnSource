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
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;

public class ParticleS2CPacket
implements Packet<ClientPlayPacketListener> {
    private double x;
    private double y;
    private double z;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float speed;
    private int count;
    private boolean longDistance;
    private ParticleEffect parameters;

    public ParticleS2CPacket() {
    }

    public <T extends ParticleEffect> ParticleS2CPacket(T arg, boolean bl, double d, double e, double f, float g, float h, float i, float j, int k) {
        this.parameters = arg;
        this.longDistance = bl;
        this.x = d;
        this.y = e;
        this.z = f;
        this.offsetX = g;
        this.offsetY = h;
        this.offsetZ = i;
        this.speed = j;
        this.count = k;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        ParticleType lv = (ParticleType)Registry.PARTICLE_TYPE.get(arg.readInt());
        if (lv == null) {
            lv = ParticleTypes.BARRIER;
        }
        this.longDistance = arg.readBoolean();
        this.x = arg.readDouble();
        this.y = arg.readDouble();
        this.z = arg.readDouble();
        this.offsetX = arg.readFloat();
        this.offsetY = arg.readFloat();
        this.offsetZ = arg.readFloat();
        this.speed = arg.readFloat();
        this.count = arg.readInt();
        this.parameters = this.readParticleParameters(arg, lv);
    }

    private <T extends ParticleEffect> T readParticleParameters(PacketByteBuf arg, ParticleType<T> arg2) {
        return arg2.getParametersFactory().read(arg2, arg);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(Registry.PARTICLE_TYPE.getRawId(this.parameters.getType()));
        arg.writeBoolean(this.longDistance);
        arg.writeDouble(this.x);
        arg.writeDouble(this.y);
        arg.writeDouble(this.z);
        arg.writeFloat(this.offsetX);
        arg.writeFloat(this.offsetY);
        arg.writeFloat(this.offsetZ);
        arg.writeFloat(this.speed);
        arg.writeInt(this.count);
        this.parameters.write(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLongDistance() {
        return this.longDistance;
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
    public float getOffsetX() {
        return this.offsetX;
    }

    @Environment(value=EnvType.CLIENT)
    public float getOffsetY() {
        return this.offsetY;
    }

    @Environment(value=EnvType.CLIENT)
    public float getOffsetZ() {
        return this.offsetZ;
    }

    @Environment(value=EnvType.CLIENT)
    public float getSpeed() {
        return this.speed;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCount() {
        return this.count;
    }

    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParameters() {
        return this.parameters;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onParticle(this);
    }
}

