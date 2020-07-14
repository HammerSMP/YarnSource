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

    public EntityPassengersSetS2CPacket(Entity entity) {
        this.id = entity.getEntityId();
        List<Entity> list = entity.getPassengerList();
        this.passengerIds = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            this.passengerIds[i] = list.get(i).getEntityId();
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.passengerIds = buf.readIntArray();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeIntArray(this.passengerIds);
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

