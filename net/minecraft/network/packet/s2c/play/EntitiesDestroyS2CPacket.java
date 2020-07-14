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

public class EntitiesDestroyS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int[] entityIds;

    public EntitiesDestroyS2CPacket() {
    }

    public EntitiesDestroyS2CPacket(int ... entityIds) {
        this.entityIds = entityIds;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityIds = new int[buf.readVarInt()];
        for (int i = 0; i < this.entityIds.length; ++i) {
            this.entityIds[i] = buf.readVarInt();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityIds.length);
        for (int i : this.entityIds) {
            buf.writeVarInt(i);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntitiesDestroy(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int[] getEntityIds() {
        return this.entityIds;
    }
}

