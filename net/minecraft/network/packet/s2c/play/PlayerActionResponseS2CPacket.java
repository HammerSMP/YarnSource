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

    public PlayerActionResponseS2CPacket(BlockPos arg, BlockState arg2, PlayerActionC2SPacket.Action arg3, boolean bl, String string) {
        this.pos = arg.toImmutable();
        this.state = arg2;
        this.action = arg3;
        this.approved = bl;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.state = Block.STATE_IDS.get(arg.readVarInt());
        this.action = arg.readEnumConstant(PlayerActionC2SPacket.Action.class);
        this.approved = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        arg.writeVarInt(Block.getRawIdFromState(this.state));
        arg.writeEnumConstant(this.action);
        arg.writeBoolean(this.approved);
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

