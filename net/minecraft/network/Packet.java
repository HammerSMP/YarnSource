/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network;

import java.io.IOException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;

public interface Packet<T extends PacketListener> {
    public void read(PacketByteBuf var1) throws IOException;

    public void write(PacketByteBuf var1) throws IOException;

    public void apply(T var1);

    default public boolean isWritingErrorSkippable() {
        return false;
    }
}

