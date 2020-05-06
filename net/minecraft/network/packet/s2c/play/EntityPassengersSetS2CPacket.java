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
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityPassengersSetS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private int[] passengerIds;

    public EntityPassengersSetS2CPacket() {
    }

    public EntityPassengersSetS2CPacket(Entity arg) {
        this.id = arg.getEntityId();
        List<Entity> list = arg.getPassengerList();
        this.passengerIds = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            this.passengerIds[i] = list.get(i).getEntityId();
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.passengerIds = arg.readIntArray();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeIntArray(this.passengerIds);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityPassengersSet(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int[] getPassengerIds() {
        return this.passengerIds;
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
}

