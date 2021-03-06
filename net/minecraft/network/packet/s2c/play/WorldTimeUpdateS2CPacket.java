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

    public WorldTimeUpdateS2CPacket(long time, long timeOfDay, boolean doDaylightCycle) {
        this.time = time;
        this.timeOfDay = timeOfDay;
        if (!doDaylightCycle) {
            this.timeOfDay = -this.timeOfDay;
            if (this.timeOfDay == 0L) {
                this.timeOfDay = -1L;
            }
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.time = buf.readLong();
        this.timeOfDay = buf.readLong();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeLong(this.time);
        buf.writeLong(this.timeOfDay);
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

