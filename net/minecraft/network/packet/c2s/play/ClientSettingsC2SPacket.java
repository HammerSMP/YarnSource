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
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Arm;

public class ClientSettingsC2SPacket
implements Packet<ServerPlayPacketListener> {
    private String language;
    private int viewDistance;
    private ChatVisibility chatVisibility;
    private boolean chatColors;
    private int playerModelBitMask;
    private Arm mainArm;

    public ClientSettingsC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public ClientSettingsC2SPacket(String string, int i, ChatVisibility arg, boolean bl, int j, Arm arg2) {
        this.language = string;
        this.viewDistance = i;
        this.chatVisibility = arg;
        this.chatColors = bl;
        this.playerModelBitMask = j;
        this.mainArm = arg2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.language = arg.readString(16);
        this.viewDistance = arg.readByte();
        this.chatVisibility = arg.readEnumConstant(ChatVisibility.class);
        this.chatColors = arg.readBoolean();
        this.playerModelBitMask = arg.readUnsignedByte();
        this.mainArm = arg.readEnumConstant(Arm.class);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(this.language);
        arg.writeByte(this.viewDistance);
        arg.writeEnumConstant(this.chatVisibility);
        arg.writeBoolean(this.chatColors);
        arg.writeByte(this.playerModelBitMask);
        arg.writeEnumConstant(this.mainArm);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onClientSettings(this);
    }

    public String getLanguage() {
        return this.language;
    }

    public ChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    public boolean hasChatColors() {
        return this.chatColors;
    }

    public int getPlayerModelBitMask() {
        return this.playerModelBitMask;
    }

    public Arm getMainArm() {
        return this.mainArm;
    }
}

