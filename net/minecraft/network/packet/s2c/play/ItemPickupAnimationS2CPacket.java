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

public class ItemPickupAnimationS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entityId;
    private int collectorEntityId;
    private int stackAmount;

    public ItemPickupAnimationS2CPacket() {
    }

    public ItemPickupAnimationS2CPacket(int entityId, int collectorId, int stackAmount) {
        this.entityId = entityId;
        this.collectorEntityId = collectorId;
        this.stackAmount = stackAmount;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.collectorEntityId = buf.readVarInt();
        this.stackAmount = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.collectorEntityId);
        buf.writeVarInt(this.stackAmount);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onItemPickupAnimation(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCollectorEntityId() {
        return this.collectorEntityId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getStackAmount() {
        return this.stackAmount;
    }
}

