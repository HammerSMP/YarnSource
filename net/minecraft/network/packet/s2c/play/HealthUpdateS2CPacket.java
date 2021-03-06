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

public class HealthUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private float health;
    private int food;
    private float saturation;

    public HealthUpdateS2CPacket() {
    }

    public HealthUpdateS2CPacket(float health, int food, float saturation) {
        this.health = health;
        this.food = food;
        this.saturation = saturation;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.health = buf.readFloat();
        this.food = buf.readVarInt();
        this.saturation = buf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeFloat(this.health);
        buf.writeVarInt(this.food);
        buf.writeFloat(this.saturation);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onHealthUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHealth() {
        return this.health;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFood() {
        return this.food;
    }

    @Environment(value=EnvType.CLIENT)
    public float getSaturation() {
        return this.saturation;
    }
}

