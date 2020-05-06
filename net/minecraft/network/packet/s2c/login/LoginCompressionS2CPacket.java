/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.login;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;

public class LoginCompressionS2CPacket
implements Packet<ClientLoginPacketListener> {
    private int compressionThreshold;

    public LoginCompressionS2CPacket() {
    }

    public LoginCompressionS2CPacket(int i) {
        this.compressionThreshold = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.compressionThreshold = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.compressionThreshold);
    }

    @Override
    public void apply(ClientLoginPacketListener arg) {
        arg.onCompression(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}

