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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private BlockPos pos;
    private int blockEntityType;
    private CompoundTag tag;

    public BlockEntityUpdateS2CPacket() {
    }

    public BlockEntityUpdateS2CPacket(BlockPos pos, int blockEntityType, CompoundTag tag) {
        this.pos = pos;
        this.blockEntityType = blockEntityType;
        this.tag = tag;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.blockEntityType = buf.readUnsignedByte();
        this.tag = buf.readCompoundTag();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeByte((byte)this.blockEntityType);
        buf.writeCompoundTag(this.tag);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onBlockEntityUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public int getBlockEntityType() {
        return this.blockEntityType;
    }

    @Environment(value=EnvType.CLIENT)
    public CompoundTag getCompoundTag() {
        return this.tag;
    }
}

