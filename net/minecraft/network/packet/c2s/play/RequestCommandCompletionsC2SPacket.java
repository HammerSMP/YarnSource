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

public class RequestCommandCompletionsC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int completionId;
    private String partialCommand;

    public RequestCommandCompletionsC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public RequestCommandCompletionsC2SPacket(int i, String string) {
        this.completionId = i;
        this.partialCommand = string;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.completionId = arg.readVarInt();
        this.partialCommand = arg.readString(32500);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.completionId);
        arg.writeString(this.partialCommand, 32500);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onRequestCommandCompletions(this);
    }

    public int getCompletionId() {
        return this.completionId;
    }

    public String getPartialCommand() {
        return this.partialCommand;
    }
}

