/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingProgressS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entityId;
    private BlockPos pos;
    private int progress;

    public BlockBreakingProgressS2CPacket() {
    }

    public BlockBreakingProgressS2CPacket(int i, BlockPos arg, int j) {
        this.entityId = i;
        this.pos = arg;
        this.progress = j;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityId = arg.readVarInt();
        this.pos = arg.readBlockPos();
        this.progress = arg.readUnsignedByte();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityId);
        arg.writeBlockPos(this.pos);
        arg.writeByte(this.progress);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onBlockDestroyProgress(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public int getProgress() {
        return this.progress;
    }
}

