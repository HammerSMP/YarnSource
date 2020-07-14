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
import net.minecraft.tag.TagManager;

public class SynchronizeTagsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private TagManager tagManager;

    public SynchronizeTagsS2CPacket() {
    }

    public SynchronizeTagsS2CPacket(TagManager arg) {
        this.tagManager = arg;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.tagManager = TagManager.fromPacket(buf);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        this.tagManager.toPacket(buf);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSynchronizeTags(this);
    }

    @Environment(value=EnvType.CLIENT)
    public TagManager getTagManager() {
        return this.tagManager;
    }
}

