/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TeamS2CPacket
implements Packet<ClientPlayPacketListener> {
    private String teamName = "";
    private Text displayName = LiteralText.EMPTY;
    private Text prefix = LiteralText.EMPTY;
    private Text suffix = LiteralText.EMPTY;
    private String nameTagVisibilityRule;
    private String collisionRule;
    private Formatting color;
    private final Collection<String> playerList;
    private int mode;
    private int flags;

    public TeamS2CPacket() {
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = Formatting.RESET;
        this.playerList = Lists.newArrayList();
    }

    public TeamS2CPacket(Team arg, int i) {
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = Formatting.RESET;
        this.playerList = Lists.newArrayList();
        this.teamName = arg.getName();
        this.mode = i;
        if (i == 0 || i == 2) {
            this.displayName = arg.getDisplayName();
            this.flags = arg.getFriendlyFlagsBitwise();
            this.nameTagVisibilityRule = arg.getNameTagVisibilityRule().name;
            this.collisionRule = arg.getCollisionRule().name;
            this.color = arg.getColor();
            this.prefix = arg.getPrefix();
            this.suffix = arg.getSuffix();
        }
        if (i == 0) {
            this.playerList.addAll(arg.getPlayerList());
        }
    }

    public TeamS2CPacket(Team arg, Collection<String> collection, int i) {
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = Formatting.RESET;
        this.playerList = Lists.newArrayList();
        if (i != 3 && i != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
        this.mode = i;
        this.teamName = arg.getName();
        this.playerList.addAll(collection);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.teamName = arg.readString(16);
        this.mode = arg.readByte();
        if (this.mode == 0 || this.mode == 2) {
            this.displayName = arg.readText();
            this.flags = arg.readByte();
            this.nameTagVisibilityRule = arg.readString(40);
            this.collisionRule = arg.readString(40);
            this.color = arg.readEnumConstant(Formatting.class);
            this.prefix = arg.readText();
            this.suffix = arg.readText();
        }
        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            int i = arg.readVarInt();
            for (int j = 0; j < i; ++j) {
                this.playerList.add(arg.readString(40));
            }
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(this.teamName);
        arg.writeByte(this.mode);
        if (this.mode == 0 || this.mode == 2) {
            arg.writeText(this.displayName);
            arg.writeByte(this.flags);
            arg.writeString(this.nameTagVisibilityRule);
            arg.writeString(this.collisionRule);
            arg.writeEnumConstant(this.color);
            arg.writeText(this.prefix);
            arg.writeText(this.suffix);
        }
        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            arg.writeVarInt(this.playerList.size());
            for (String string : this.playerList) {
                arg.writeString(string);
            }
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onTeam(this);
    }

    @Environment(value=EnvType.CLIENT)
    public String getTeamName() {
        return this.teamName;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDisplayName() {
        return this.displayName;
    }

    @Environment(value=EnvType.CLIENT)
    public Collection<String> getPlayerList() {
        return this.playerList;
    }

    @Environment(value=EnvType.CLIENT)
    public int getMode() {
        return this.mode;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFlags() {
        return this.flags;
    }

    @Environment(value=EnvType.CLIENT)
    public Formatting getPlayerPrefix() {
        return this.color;
    }

    @Environment(value=EnvType.CLIENT)
    public String getNameTagVisibilityRule() {
        return this.nameTagVisibilityRule;
    }

    @Environment(value=EnvType.CLIENT)
    public String getCollisionRule() {
        return this.collisionRule;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getPrefix() {
        return this.prefix;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getSuffix() {
        return this.suffix;
    }
}

