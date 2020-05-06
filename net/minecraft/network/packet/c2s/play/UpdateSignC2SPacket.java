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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class UpdateSignC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private String[] text;

    public UpdateSignC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateSignC2SPacket(BlockPos arg, Text arg2, Text arg3, Text arg4, Text arg5) {
        this.pos = arg;
        this.text = new String[]{arg2.getString(), arg3.getString(), arg4.getString(), arg5.getString()};
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.text = new String[4];
        for (int i = 0; i < 4; ++i) {
            this.text[i] = arg.readString(384);
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        for (int i = 0; i < 4; ++i) {
            arg.writeString(this.text[i]);
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

