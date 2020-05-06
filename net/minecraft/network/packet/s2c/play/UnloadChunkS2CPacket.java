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

public class UnloadChunkS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int x;
    private int z;

    public UnloadChunkS2CPacket() {
    }

    public UnloadChunkS2CPacket(int i, int j) {
        this.x = i;
        this.z = j;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.x = arg.readInt();
        this.z = arg.readInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(this.x);
        arg.writeInt(this.z);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onUnloadChunk(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getX() {
        return this.x;
    }

    @Environment(value=EnvType.CLIENT)
    public int getZ() {
        return this.z;
    }
}

