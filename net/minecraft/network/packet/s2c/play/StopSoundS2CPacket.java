/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class StopSoundS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Identifier soundId;
    private SoundCategory category;

    public StopSoundS2CPacket() {
    }

    public StopSoundS2CPacket(@Nullable Identifier arg, @Nullable SoundCategory arg2) {
        this.soundId = arg;
        this.category = arg2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        byte i = arg.readByte();
        if ((i & 1) > 0) {
            this.category = arg.readEnumConstant(SoundCategory.class);
        }
        if ((i & 2) > 0) {
            this.soundId = arg.readIdentifier();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        if (this.category != null) {
            if (this.soundId != null) {
                arg.writeByte(3);
                arg.writeEnumConstant(this.category);
                arg.writeIdentifier(this.soundId);
            } else {
                arg.writeByte(1);
                arg.writeEnumConstant(this.category);
            }
        } else if (this.soundId != null) {
            arg.writeByte(2);
            arg.writeIdentifier(this.soundId);
        } else {
            arg.writeByte(0);
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Identifier getSoundId() {
        return this.soundId;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public SoundCategory getCategory() {
        return this.category;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onStopSound(this);
    }
}

