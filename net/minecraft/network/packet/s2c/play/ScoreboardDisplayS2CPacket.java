/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreboardDisplayS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int slot;
    private String name;

    public ScoreboardDisplayS2CPacket() {
    }

    public ScoreboardDisplayS2CPacket(int i, @Nullable ScoreboardObjective arg) {
        this.slot = i;
        this.name = arg == null ? "" : arg.getName();
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.slot = arg.readByte();
        this.name = arg.readString(16);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.slot);
        arg.writeString(this.name);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onScoreboardDisplay(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public String getName() {
        return Objects.equals(this.name, "") ? null : this.name;
    }
}

