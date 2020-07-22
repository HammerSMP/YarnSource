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

public class QueryBlockNbtC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int transactionId;
    private BlockPos pos;

    public QueryBlockNbtC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public QueryBlockNbtC2SPacket(int transactionId, BlockPos pos) {
        this.transactionId = transactionId;
        this.pos = pos;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.transactionId = buf.readVarInt();
        this.pos = buf.readBlockPos();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.transactionId);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onQueryBlockNbt(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

