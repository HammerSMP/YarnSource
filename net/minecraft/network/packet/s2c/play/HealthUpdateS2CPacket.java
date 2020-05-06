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

    public HealthUpdateS2CPacket(float f, int i, float g) {
        this.health = f;
        this.food = i;
        this.saturation = g;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.health = arg.readFloat();
        this.food = arg.readVarInt();
        this.saturation = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeFloat(this.health);
        arg.writeVarInt(this.food);
        arg.writeFloat(this.saturation);
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

