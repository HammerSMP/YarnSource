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
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundFromEntityS2CPacket
implements Packet<ClientPlayPacketListener> {
    private SoundEvent sound;
    private SoundCategory category;
    private int entityId;
    private float volume;
    private float pitch;

    public PlaySoundFromEntityS2CPacket() {
    }

    public PlaySoundFromEntityS2CPacket(SoundEvent arg, SoundCategory arg2, Entity arg3, float f, float g) {
        Validate.notNull((Object)arg, (String)"sound", (Object[])new Object[0]);
        this.sound = arg;
        this.category = arg2;
        this.entityId = arg3.getEntityId();
        this.volume = f;
        this.pitch = g;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.sound = (SoundEvent)Registry.SOUND_EVENT.get(arg.readVarInt());
        this.category = arg.readEnumConstant(SoundCategory.class);
        this.entityId = arg.readVarInt();
        this.volume = arg.readFloat();
        this.pitch = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
        arg.writeEnumConstant(this.category);
        arg.writeVarInt(this.entityId);
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
    public int getEntityId() {
        return this.entityId;
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
        arg.onPlaySoundFromEntity(this);
    }
}

