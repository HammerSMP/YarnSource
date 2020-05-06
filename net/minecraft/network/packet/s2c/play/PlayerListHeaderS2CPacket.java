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
import net.minecraft.text.Text;

public class PlayerListHeaderS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Text header;
    private Text footer;

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.header = arg.readText();
        this.footer = arg.readText();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeText(this.header);
        arg.writeText(this.footer);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerListHeader(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getHeader() {
        return this.header;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getFooter() {
        return this.footer;
    }
}

