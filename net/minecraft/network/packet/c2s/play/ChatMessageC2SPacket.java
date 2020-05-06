/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ChatMessageC2SPacket
implements Packet<ServerPlayPacketListener> {
    private String chatMessage;

    public ChatMessageC2SPacket() {
    }

    public ChatMessageC2SPacket(String string) {
        if (string.length() > 256) {
            string = string.substring(0, 256);
        }
        this.chatMessage = string;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.chatMessage = arg.readString(256);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(this.chatMessage);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onGameMessage(this);
    }

    public String getChatMessage() {
        return this.chatMessage;
    }
}

