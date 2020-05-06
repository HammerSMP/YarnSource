/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityTrackerUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private List<DataTracker.Entry<?>> trackedValues;

    public EntityTrackerUpdateS2CPacket() {
    }

    public EntityTrackerUpdateS2CPacket(int i, DataTracker arg, boolean bl) {
        this.id = i;
        if (bl) {
            this.trackedValues = arg.getAllEntries();
            arg.clearDirty();
        } else {
            this.trackedValues = arg.getDirtyEntries();
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.trackedValues = DataTracker.deserializePacket(arg);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        DataTracker.entriesToPacket(this.trackedValues, arg);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityTrackerUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public List<DataTracker.Entry<?>> getTrackedValues() {
        return this.trackedValues;
    }

    @Environment(value=EnvType.CLIENT)
    public int id() {
        return this.id;
    }
}

