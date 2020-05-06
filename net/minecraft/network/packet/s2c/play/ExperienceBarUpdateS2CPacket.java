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

public class ExperienceBarUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private float barProgress;
    private int experienceLevel;
    private int experience;

    public ExperienceBarUpdateS2CPacket() {
    }

    public ExperienceBarUpdateS2CPacket(float f, int i, int j) {
        this.barProgress = f;
        this.experienceLevel = i;
        this.experience = j;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.barProgress = arg.readFloat();
        this.experience = arg.readVarInt();
        this.experienceLevel = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeFloat(this.barProgress);
        arg.writeVarInt(this.experience);
        arg.writeVarInt(this.experienceLevel);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onExperienceBarUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public float getBarProgress() {
        return this.barProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public int getExperienceLevel() {
        return this.experienceLevel;
    }

    @Environment(value=EnvType.CLIENT)
    public int getExperience() {
        return this.experience;
    }
}

