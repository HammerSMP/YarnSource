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

public class ResourcePackSendS2CPacket
implements Packet<ClientPlayPacketListener> {
    private String url;
    private String hash;

    public ResourcePackSendS2CPacket() {
    }

    public ResourcePackSendS2CPacket(String string, String string2) {
        this.url = string;
        this.hash = string2;
        if (string2.length() > 40) {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + string2.length() + ")");
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.url = arg.readString(32767);
        this.hash = arg.readString(40);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(this.url);
        arg.writeString(this.hash);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onResourcePackSend(this);
    }

    @Environment(value=EnvType.CLIENT)
    public String getURL() {
        return this.url;
    }

    @Environment(value=EnvType.CLIENT)
    public String getSHA1() {
        return this.hash;
    }
}

