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

    public ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode arg, @Nullable String string, String string2, int i) {
        if (arg != ServerScoreboard.UpdateMode.REMOVE && string == null) {
            throw new IllegalArgumentException("Need an objective name");
        }
        this.playerName = string2;
        this.objectiveName = string;
        this.score = i;
        this.mode = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.playerName = arg.readString(40);
        this.mode = arg.readEnumConstant(ServerScoreboard.UpdateMode.class);
        String string = arg.readString(16);
        String string2 = this.objectiveName = Objects.equals(string, "") ? null : string;
        if (this.mode != ServerScoreboard.UpdateMode.REMOVE) {
            this.score = arg.readVarInt();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(this.playerName);
        arg.writeEnumConstant(this.mode);
        arg.writeString(this.objectiveName == null ? "" : this.objectiveName);
        if (this.mode != ServerScoreboard.UpdateMode.REMOVE) {
            arg.writeVarInt(this.score);
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

