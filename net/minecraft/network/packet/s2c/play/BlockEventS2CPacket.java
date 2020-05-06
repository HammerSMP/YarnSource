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

    public BlockEventS2CPacket(BlockPos arg, Block arg2, int i, int j) {
        this.pos = arg;
        this.block = arg2;
        this.type = i;
        this.data = j;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.type = arg.readUnsignedByte();
        this.data = arg.readUnsignedByte();
        this.block = Registry.BLOCK.get(arg.readVarInt());
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        arg.writeByte(this.type);
        arg.writeByte(this.data);
        arg.writeVarInt(Registry.BLOCK.getRawId(this.block));
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

