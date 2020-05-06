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
import net.minecraft.util.math.Direction;

public class PlayerActionC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private Direction direction;
    private Action action;

    public PlayerActionC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerActionC2SPacket(Action arg, BlockPos arg2, Direction arg3) {
        this.action = arg;
        this.pos = arg2.toImmutable();
        this.direction = arg3;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.action = arg.readEnumConstant(Action.class);
        this.pos = arg.readBlockPos();
        this.direction = Direction.byId(arg.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.action);
        arg.writeBlockPos(this.pos);
        arg.writeByte(this.direction.getId());
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onPlayerAction(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS;

    }
}

