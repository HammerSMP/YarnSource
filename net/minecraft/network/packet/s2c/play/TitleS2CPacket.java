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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class TitleS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private Text text;
    private int fadeInTicks;
    private int stayTicks;
    private int fadeOutTicks;

    public TitleS2CPacket() {
    }

    public TitleS2CPacket(Action action, Text text) {
        this(action, text, -1, -1, -1);
    }

    public TitleS2CPacket(int fadeInTicks, int stayTicks, int fadeOutTicks) {
        this(Action.TIMES, null, fadeInTicks, stayTicks, fadeOutTicks);
    }

    public TitleS2CPacket(Action action, @Nullable Text text, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        this.action = action;
        this.text = text;
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.action = buf.readEnumConstant(Action.class);
        if (this.action == Action.TITLE || this.action == Action.SUBTITLE || this.action == Action.ACTIONBAR) {
            this.text = buf.readText();
        }
        if (this.action == Action.TIMES) {
            this.fadeInTicks = buf.readInt();
            this.stayTicks = buf.readInt();
            this.fadeOutTicks = buf.readInt();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        if (this.action == Action.TITLE || this.action == Action.SUBTITLE || this.action == Action.ACTIONBAR) {
            buf.writeText(this.text);
        }
        if (this.action == Action.TIMES) {
            buf.writeInt(this.fadeInTicks);
            buf.writeInt(this.stayTicks);
            buf.writeInt(this.fadeOutTicks);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onTitle(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getText() {
        return this.text;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFadeInTicks() {
        return this.fadeInTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public int getStayTicks() {
        return this.stayTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFadeOutTicks() {
        return this.fadeOutTicks;
    }

    public static enum Action {
        TITLE,
        SUBTITLE,
        ACTIONBAR,
        TIMES,
        CLEAR,
        RESET;

    }
}

