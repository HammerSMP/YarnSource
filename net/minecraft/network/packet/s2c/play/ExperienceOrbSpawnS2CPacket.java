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
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ExperienceOrbSpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private double x;
    private double y;
    private double z;
    private int experience;

    public ExperienceOrbSpawnS2CPacket() {
    }

    public ExperienceOrbSpawnS2CPacket(ExperienceOrbEntity arg) {
        this.id = arg.getEntityId();
        this.x = arg.getX();
        this.y = arg.getY();
        this.z = arg.getZ();
        this.experience = arg.getExperienceAmount();
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.x = arg.readDouble();
        this.y = arg.readDouble();
        this.z = arg.readDouble();
        this.experience = arg.readShort();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeDouble(this.x);
        arg.writeDouble(this.y);
        arg.writeDouble(this.z);
        arg.writeShort(this.experience);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onExperienceOrbSpawn(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
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
    public int getExperience() {
        return this.experience;
    }
}

