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

    public EntitiesDestroyS2CPacket(int ... is) {
        this.entityIds = is;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityIds = new int[arg.readVarInt()];
        for (int i = 0; i < this.entityIds.length; ++i) {
            this.entityIds[i] = arg.readVarInt();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityIds.length);
        for (int i : this.entityIds) {
            arg.writeVarInt(i);
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

