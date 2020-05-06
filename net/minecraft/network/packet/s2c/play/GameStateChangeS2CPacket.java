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

public class GameStateChangeS2CPacket
implements Packet<ClientPlayPacketListener> {
    public static final String[] REASON_MESSAGES = new String[]{"block.minecraft.spawn.not_valid"};
    private int reason;
    private float value;

    public GameStateChangeS2CPacket() {
    }

    public GameStateChangeS2CPacket(int i, float f) {
        this.reason = i;
        this.value = f;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.reason = arg.readUnsignedByte();
        this.value = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.reason);
        arg.writeFloat(this.value);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onGameStateChange(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getReason() {
        return this.reason;
    }

    @Environment(value=EnvType.CLIENT)
    public float getValue() {
        return this.value;
    }
}

