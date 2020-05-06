/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.login;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;

public class LoginQueryResponseC2SPacket
implements Packet<ServerLoginPacketListener> {
    private int queryId;
    private PacketByteBuf response;

    public LoginQueryResponseC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public LoginQueryResponseC2SPacket(int i, @Nullable PacketByteBuf arg) {
        this.queryId = i;
        this.response = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.queryId = arg.readVarInt();
        if (arg.readBoolean()) {
            int i = arg.readableBytes();
            if (i < 0 || i > 0x100000) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }
            this.response = new PacketByteBuf(arg.readBytes(i));
        } else {
            this.response = null;
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.queryId);
        if (this.response != null) {
            arg.writeBoolean(true);
            arg.writeBytes(this.response.copy());
        } else {
            arg.writeBoolean(false);
        }
    }

    @Override
    public void apply(ServerLoginPacketListener arg) {
        arg.onQueryResponse(this);
    }
}

