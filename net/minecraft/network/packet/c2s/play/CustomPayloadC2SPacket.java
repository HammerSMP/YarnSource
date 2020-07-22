/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;

public class CustomPayloadC2SPacket
implements Packet<ServerPlayPacketListener> {
    public static final Identifier BRAND = new Identifier("brand");
    private Identifier channel;
    private PacketByteBuf data;

    public CustomPayloadC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public CustomPayloadC2SPacket(Identifier channel, PacketByteBuf data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.channel = buf.readIdentifier();
        int i = buf.readableBytes();
        if (i < 0 || i > 32767) {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
        this.data = new PacketByteBuf(buf.readBytes(i));
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeIdentifier(this.channel);
        buf.writeBytes(this.data);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onCustomPayload(this);
        if (this.data != null) {
            this.data.release();
        }
    }
}

