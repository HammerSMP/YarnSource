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
import net.minecraft.tag.RegistryTagManager;

public class SynchronizeTagsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private RegistryTagManager tagManager;

    public SynchronizeTagsS2CPacket() {
    }

    public SynchronizeTagsS2CPacket(RegistryTagManager arg) {
        this.tagManager = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.tagManager = RegistryTagManager.fromPacket(arg);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        this.tagManager.toPacket(arg);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSynchronizeTags(this);
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryTagManager getTagManager() {
        return this.tagManager;
    }
}
