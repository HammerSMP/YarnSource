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
import net.minecraft.class_5415;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class SynchronizeTagsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private class_5415 tagManager;

    public SynchronizeTagsS2CPacket() {
    }

    public SynchronizeTagsS2CPacket(class_5415 arg) {
        this.tagManager = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.tagManager = class_5415.method_30219(arg);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        this.tagManager.method_30217(arg);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSynchronizeTags(this);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5415 getTagManager() {
        return this.tagManager;
    }
}

