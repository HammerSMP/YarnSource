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
    public CustomPayloadC2SPacket(Identifier arg, PacketByteBuf arg2) {
        this.channel = arg;
        this.data = arg2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.channel = arg.readIdentifier();
        int i = arg.readableBytes();
        if (i < 0 || i > 32767) {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
        this.data = new PacketByteBuf(arg.readBytes(i));
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeIdentifier(this.channel);
        arg.writeBytes(this.data);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onCustomPayload(this);
        if (this.data != null) {
            this.data.release();
        }
    }
}

