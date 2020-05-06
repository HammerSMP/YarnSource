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
import net.minecraft.util.Identifier;

public class SelectAdvancementTabS2CPacket
implements Packet<ClientPlayPacketListener> {
    @Nullable
    private Identifier tabId;

    public SelectAdvancementTabS2CPacket() {
    }

    public SelectAdvancementTabS2CPacket(@Nullable Identifier arg) {
        this.tabId = arg;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSelectAdvancementTab(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        if (arg.readBoolean()) {
            this.tabId = arg.readIdentifier();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBoolean(this.tabId != null);
        if (this.tabId != null) {
            arg.writeIdentifier(this.tabId);
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Identifier getTabId() {
        return this.tabId;
    }
}

