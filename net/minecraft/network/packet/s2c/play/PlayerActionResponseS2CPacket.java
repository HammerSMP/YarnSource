/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
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
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerActionResponseS2CPacket
implements Packet<ClientPlayPacketListener> {
    private static final Logger LOGGER = LogManager.getLogger();
    private BlockPos pos;
    private BlockState state;
    PlayerActionC2SPacket.Action action;
    private boolean approved;

    public PlayerActionResponseS2CPacket() {
    }

    public PlayerActionResponseS2CPacket(BlockPos pos, BlockState state, PlayerActionC2SPacket.Action action, boolean approved, String reason) {
        this.pos = pos.toImmutable();
        this.state = state;
        this.action = action;
        this.approved = approved;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.state = Block.STATE_IDS.get(buf.readVarInt());
        this.action = buf.readEnumConstant(PlayerActionC2SPacket.Action.class);
        this.approved = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeVarInt(Block.getRawIdFromState(this.state));
        buf.writeEnumConstant(this.action);
        buf.writeBoolean(this.approved);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerActionResponse(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockState getBlockState() {
        return this.state;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getBlockPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isApproved() {
        return this.approved;
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerActionC2SPacket.Action getAction() {
        return this.action;
    }
}

