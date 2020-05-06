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

public class WorldTimeUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private long time;
    private long timeOfDay;

    public WorldTimeUpdateS2CPacket() {
    }

    public WorldTimeUpdateS2CPacket(long l, long m, boolean bl) {
        this.time = l;
        this.timeOfDay = m;
        if (!bl) {
            this.timeOfDay = -this.timeOfDay;
            if (this.timeOfDay == 0L) {
                this.timeOfDay = -1L;
            }
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.time = arg.readLong();
        this.timeOfDay = arg.readLong();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeLong(this.time);
        arg.writeLong(this.timeOfDay);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onWorldTimeUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public long getTime() {
        return this.time;
    }

    @Environment(value=EnvType.CLIENT)
    public long getTimeOfDay() {
        return this.timeOfDay;
    }
}

