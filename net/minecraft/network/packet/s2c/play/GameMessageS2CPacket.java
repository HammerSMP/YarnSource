/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class GameMessageS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Text message;
    private MessageType location;
    private UUID field_25133;

    public GameMessageS2CPacket() {
    }

    public GameMessageS2CPacket(Text arg, MessageType arg2, UUID uUID) {
        this.message = arg;
        this.location = arg2;
        this.field_25133 = uUID;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.message = arg.readText();
        this.location = MessageType.byId(arg.readByte());
        this.field_25133 = arg.readUuid();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeText(this.message);
        arg.writeByte(this.location.getId());
        arg.writeUuid(this.field_25133);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onGameMessage(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getMessage() {
        return this.message;
    }

    public boolean isNonChat() {
        return this.location == MessageType.SYSTEM || this.location == MessageType.GAME_INFO;
    }

    public MessageType getLocation() {
        return this.location;
    }

    @Environment(value=EnvType.CLIENT)
    public UUID method_29175() {
        return this.field_25133;
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return true;
    }
}

