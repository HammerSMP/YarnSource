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
import net.minecraft.util.math.BlockPos;

public class UpdateSignC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private String[] text;

    public UpdateSignC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateSignC2SPacket(BlockPos pos, String line1, String line2, String line3, String line4) {
        this.pos = pos;
        this.text = new String[]{line1, line2, line3, line4};
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.text = new String[4];
        for (int i = 0; i < 4; ++i) {
            this.text[i] = buf.readString(384);
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        for (int i = 0; i < 4; ++i) {
            buf.writeString(this.text[i]);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onSignUpdate(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public String[] getText() {
        return this.text;
    }
}

