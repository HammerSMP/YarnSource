/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public class ScoreboardObjectiveUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private String name;
    private Text displayName;
    private ScoreboardCriterion.RenderType type;
    private int mode;

    public ScoreboardObjectiveUpdateS2CPacket() {
    }

    public ScoreboardObjectiveUpdateS2CPacket(ScoreboardObjective arg, int i) {
        this.name = arg.getName();
        this.displayName = arg.getDisplayName();
        this.type = arg.getRenderType();
        this.mode = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.name = arg.readString(16);
        this.mode = arg.readByte();
        if (this.mode == 0 || this.mode == 2) {
            this.displayName = arg.readText();
            this.type = arg.readEnumConstant(ScoreboardCriterion.RenderType.class);
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(this.name);
        arg.writeByte(this.mode);
        if (this.mode == 0 || this.mode == 2) {
            arg.writeText(this.displayName);
            arg.writeEnumConstant(this.type);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onScoreboardObjectiveUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public String getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDisplayName() {
        return this.displayName;
    }

    @Environment(value=EnvType.CLIENT)
    public int getMode() {
        return this.mode;
    }

    @Environment(value=EnvType.CLIENT)
    public ScoreboardCriterion.RenderType getType() {
        return this.type;
    }
}

