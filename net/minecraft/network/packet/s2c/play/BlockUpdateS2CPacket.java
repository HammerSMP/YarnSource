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
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BlockUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private BlockPos pos;
    private BlockState state;

    public BlockUpdateS2CPacket() {
    }

    public BlockUpdateS2CPacket(BlockPos arg, BlockState arg2) {
        this.pos = arg;
        this.state = arg2;
    }

    public BlockUpdateS2CPacket(BlockView arg, BlockPos arg2) {
        this(arg2, arg.getBlockState(arg2));
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.state = Block.STATE_IDS.get(arg.readVarInt());
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        arg.writeVarInt(Block.getRawIdFromState(this.state));
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onBlockUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockState getState() {
        return this.state;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}

