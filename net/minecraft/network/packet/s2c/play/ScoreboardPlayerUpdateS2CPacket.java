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
import net.minecraft.scoreboard.ServerScoreboard;

public class ScoreboardPlayerUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private String playerName = "";
    @Nullable
    private String objectiveName;
    private int score;
    private ServerScoreboard.UpdateMode mode;

    public ScoreboardPlayerUpdateS2CPacket() {
    }

    public ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode mode, @Nullable String objectiveName, String playerName, int score) {
        if (mode != ServerScoreboard.UpdateMode.REMOVE && objectiveName == null) {
            throw new IllegalArgumentException("Need an objective name");
        }
        this.playerName = playerName;
        this.objectiveName = objectiveName;
        this.score = score;
        this.mode = mode;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.playerName = buf.readString(40);
        this.mode = buf.readEnumConstant(ServerScoreboard.UpdateMode.class);
        String string = buf.readString(16);
        String string2 = this.objectiveName = Objects.equals(string, "") ? null : string;
        if (this.mode != ServerScoreboard.UpdateMode.REMOVE) {
            this.score = buf.readVarInt();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeString(this.playerName);
        buf.writeEnumConstant(this.mode);
        buf.writeString(this.objectiveName == null ? "" : this.objectiveName);
        if (this.mode != ServerScoreboard.UpdateMode.REMOVE) {
            buf.writeVarInt(this.score);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onScoreboardPlayerUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public String getPlayerName() {
        return this.playerName;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public String getObjectiveName() {
        return this.objectiveName;
    }

    @Environment(value=EnvType.CLIENT)
    public int getScore() {
        return this.score;
    }

    @Environment(value=EnvType.CLIENT)
    public ServerScoreboard.UpdateMode getUpdateMode() {
        return this.mode;
    }
}

