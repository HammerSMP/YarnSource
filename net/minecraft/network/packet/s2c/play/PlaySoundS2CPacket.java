/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundS2CPacket
implements Packet<ClientPlayPacketListener> {
    private SoundEvent sound;
    private SoundCategory category;
    private int fixedX;
    private int fixedY;
    private int fixedZ;
    private float volume;
    private float pitch;

    public PlaySoundS2CPacket() {
    }

    public PlaySoundS2CPacket(SoundEvent arg, SoundCategory arg2, double d, double e, double f, float g, float h) {
        Validate.notNull((Object)arg, (String)"sound", (Object[])new Object[0]);
        this.sound = arg;
        this.category = arg2;
        this.fixedX = (int)(d * 8.0);
        this.fixedY = (int)(e * 8.0);
        this.fixedZ = (int)(f * 8.0);
        this.volume = g;
        this.pitch = h;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.sound = (SoundEvent)Registry.SOUND_EVENT.get(arg.readVarInt());
        this.category = arg.readEnumConstant(SoundCategory.class);
        this.fixedX = arg.readInt();
        this.fixedY = arg.readInt();
        this.fixedZ = arg.readInt();
        this.volume = arg.readFloat();
        this.pitch = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
        arg.writeEnumConstant(this.category);
        arg.writeInt(this.fixedX);
        arg.writeInt(this.fixedY);
        arg.writeInt(this.fixedZ);
        arg.writeFloat(this.volume);
        arg.writeFloat(this.pitch);
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
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
        arg.onPlaySound(this);
    }
}

