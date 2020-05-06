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
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ClientCommandC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int entityId;
    private Mode mode;
    private int mountJumpHeight;

    public ClientCommandC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public ClientCommandC2SPacket(Entity arg, Mode arg2) {
        this(arg, arg2, 0);
    }

    @Environment(value=EnvType.CLIENT)
    public ClientCommandC2SPacket(Entity arg, Mode arg2, int i) {
        this.entityId = arg.getEntityId();
        this.mode = arg2;
        this.mountJumpHeight = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityId = arg.readVarInt();
        this.mode = arg.readEnumConstant(Mode.class);
        this.mountJumpHeight = arg.readVarInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityId);
        arg.writeEnumConstant(this.mode);
        arg.writeVarInt(this.mountJumpHeight);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onClientCommand(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public int getMountJumpHeight() {
        return this.mountJumpHeight;
    }

    public static enum Mode {
        PRESS_SHIFT_KEY,
        RELEASE_SHIFT_KEY,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        START_RIDING_JUMP,
        STOP_RIDING_JUMP,
        OPEN_INVENTORY,
        START_FALL_FLYING;

    }
}

