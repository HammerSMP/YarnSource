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
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PlaySoundIdS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Identifier id;
    private SoundCategory category;
    private int fixedX;
    private int fixedY = Integer.MAX_VALUE;
    private int fixedZ;
    private float volume;
    private float pitch;

    public PlaySoundIdS2CPacket() {
    }

    public PlaySoundIdS2CPacket(Identifier arg, SoundCategory arg2, Vec3d arg3, float f, float g) {
        this.id = arg;
        this.category = arg2;
        this.fixedX = (int)(arg3.x * 8.0);
        this.fixedY = (int)(arg3.y * 8.0);
        this.fixedZ = (int)(arg3.z * 8.0);
        this.volume = f;
        this.pitch = g;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readIdentifier();
        this.category = arg.readEnumConstant(SoundCategory.class);
        this.fixedX = arg.readInt();
        this.fixedY = arg.readInt();
        this.fixedZ = arg.readInt();
        this.volume = arg.readFloat();
        this.pitch = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeIdentifier(this.id);
        arg.writeEnumConstant(this.category);
        arg.writeInt(this.fixedX);
        arg.writeInt(this.fixedY);
        arg.writeInt(this.fixedZ);
        arg.writeFloat(this.volume);
        arg.writeFloat(this.pitch);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getSoundId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundCategory getCategory() {
        return this.category;
    }

    @Environment(value=EnvType.CLIENT)
    public double getX() {
        return (float)this.fixedX / 8.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public double getY() {
        return (float)this.fixedY / 8.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public double getZ() {
        return (float)this.fixedZ / 8.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getVolume() {
        return this.volume;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlaySoundId(this);
    }
}

