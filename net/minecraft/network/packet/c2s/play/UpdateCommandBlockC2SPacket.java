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
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class UpdateCommandBlockC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private String command;
    private boolean trackOutput;
    private boolean conditional;
    private boolean alwaysActive;
    private CommandBlockBlockEntity.Type type;

    public UpdateCommandBlockC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateCommandBlockC2SPacket(BlockPos arg, String string, CommandBlockBlockEntity.Type arg2, boolean bl, boolean bl2, boolean bl3) {
        this.pos = arg;
        this.command = string;
        this.trackOutput = bl;
        this.conditional = bl2;
        this.alwaysActive = bl3;
        this.type = arg2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.pos = arg.readBlockPos();
        this.command = arg.readString(32767);
        this.type = arg.readEnumConstant(CommandBlockBlockEntity.Type.class);
        byte i = arg.readByte();
        this.trackOutput = (i & 1) != 0;
        this.conditional = (i & 2) != 0;
        this.alwaysActive = (i & 4) != 0;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBlockPos(this.pos);
        arg.writeString(this.command);
        arg.writeEnumConstant(this.type);
        int i = 0;
        if (this.trackOutput) {
            i |= 1;
        }
        if (this.conditional) {
            i |= 2;
        }
        if (this.alwaysActive) {
            i |= 4;
        }
        arg.writeByte(i);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onUpdateCommandBlock(this);
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }

    public boolean isConditional() {
        return this.conditional;
    }

    public boolean isAlwaysActive() {
        return this.alwaysActive;
    }

    public CommandBlockBlockEntity.Type getType() {
        return this.type;
    }
}

