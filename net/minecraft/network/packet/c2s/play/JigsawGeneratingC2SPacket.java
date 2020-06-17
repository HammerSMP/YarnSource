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

public class JigsawGeneratingC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private int maxDepth;
    private boolean keepJigsaws;

    public JigsawGeneratingC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public JigsawGeneratingC2SPacket(BlockPos arg, int i, boolean bl) {
        this.pos = arg;
        this.maxDepth = i;
        this.keepJigsaws = bl;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.maxDepth = arg.readVarInt();
        this.keepJigsaws = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        arg.writeVarInt(this.maxDepth);
        arg.writeBoolean(this.keepJigsaws);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onJigsawGenerating(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public boolean shouldKeepJigsaws() {
        return this.keepJigsaws;
    }
}

