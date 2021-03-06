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

    public BlockBreakingProgressS2CPacket(int entityId, BlockPos pos, int progress) {
        this.entityId = entityId;
        this.pos = pos;
        this.progress = progress;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.pos = buf.readBlockPos();
        this.progress = buf.readUnsignedByte();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.progress);
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

