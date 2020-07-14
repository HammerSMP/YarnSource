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
import net.minecraft.text.Text;

public class LoginDisconnectS2CPacket
implements Packet<ClientLoginPacketListener> {
    private Text reason;

    public LoginDisconnectS2CPacket() {
    }

    public LoginDisconnectS2CPacket(Text reason) {
        this.reason = reason;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.reason = Text.Serializer.fromLenientJson(buf.readString(262144));
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeText(this.reason);
    }

    @Override
    public void apply(ClientLoginPacketListener arg) {
        arg.onDisconnect(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getReason() {
        return this.reason;
    }
}

