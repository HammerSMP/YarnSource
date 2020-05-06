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

    public TitleS2CPacket(Action arg, Text arg2) {
        this(arg, arg2, -1, -1, -1);
    }

    public TitleS2CPacket(int i, int j, int k) {
        this(Action.TIMES, null, i, j, k);
    }

    public TitleS2CPacket(Action arg, @Nullable Text arg2, int i, int j, int k) {
        this.action = arg;
        this.text = arg2;
        this.fadeInTicks = i;
        this.stayTicks = j;
        this.fadeOutTicks = k;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.action = arg.readEnumConstant(Action.class);
        if (this.action == Action.TITLE || this.action == Action.SUBTITLE || this.action == Action.ACTIONBAR) {
            this.text = arg.readText();
        }
        if (this.action == Action.TIMES) {
            this.fadeInTicks = arg.readInt();
            this.stayTicks = arg.readInt();
            this.fadeOutTicks = arg.readInt();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.action);
        if (this.action == Action.TITLE || this.action == Action.SUBTITLE || this.action == Action.ACTIONBAR) {
            arg.writeText(this.text);
        }
        if (this.action == Action.TIMES) {
            arg.writeInt(this.fadeInTicks);
            arg.writeInt(this.stayTicks);
            arg.writeInt(this.fadeOutTicks);
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

