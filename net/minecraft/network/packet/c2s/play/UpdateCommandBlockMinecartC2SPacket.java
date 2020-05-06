/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class UpdateCommandBlockMinecartC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int entityId;
    private String command;
    private boolean trackOutput;

    public UpdateCommandBlockMinecartC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateCommandBlockMinecartC2SPacket(int i, String string, boolean bl) {
        this.entityId = i;
        this.command = string;
        this.trackOutput = bl;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityId = arg.readVarInt();
        this.command = arg.readString(32767);
        this.trackOutput = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityId);
        arg.writeString(this.command);
        arg.writeBoolean(this.trackOutput);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onUpdateCommandBlockMinecart(this);
    }

    @Nullable
    public CommandBlockExecutor getMinecartCommandExecutor(World arg) {
        Entity lv = arg.getEntityById(this.entityId);
        if (lv instanceof CommandBlockMinecartEntity) {
            return ((CommandBlockMinecartEntity)lv).getCommandExecutor();
        }
        return null;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }
}

