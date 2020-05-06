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
import net.minecraft.util.Identifier;

public class LoginQueryRequestS2CPacket
implements Packet<ClientLoginPacketListener> {
    private int queryId;
    private Identifier channel;
    private PacketByteBuf payload;

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.queryId = arg.readVarInt();
        this.channel = arg.readIdentifier();
        int i = arg.readableBytes();
        if (i < 0 || i > 0x100000) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.payload = new PacketByteBuf(arg.readBytes(i));
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.queryId);
        arg.writeIdentifier(this.channel);
        arg.writeBytes(this.payload.copy());
    }

    @Override
    public void apply(ClientLoginPacketListener arg) {
        arg.onQueryRequest(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getQueryId() {
        return this.queryId;
    }
}

