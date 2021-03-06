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
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    private BlockPos pos;
    private int type;
    private int data;
    private Block block;

    public BlockEventS2CPacket() {
    }

    public BlockEventS2CPacket(BlockPos pos, Block block, int type, int data) {
        this.pos = pos;
        this.block = block;
        this.type = type;
        this.data = data;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.type = buf.readUnsignedByte();
        this.data = buf.readUnsignedByte();
        this.block = Registry.BLOCK.get(buf.readVarInt());
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.type);
        buf.writeByte(this.data);
        buf.writeVarInt(Registry.BLOCK.getRawId(this.block));
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onBlockEvent(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public int getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public int getData() {
        return this.data;
    }

    @Environment(value=EnvType.CLIENT)
    public Block getBlock() {
        return this.block;
    }
}

