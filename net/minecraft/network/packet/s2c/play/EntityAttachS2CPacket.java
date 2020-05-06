/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityAttachS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int attachedId;
    private int holdingId;

    public EntityAttachS2CPacket() {
    }

    public EntityAttachS2CPacket(Entity arg, @Nullable Entity arg2) {
        this.attachedId = arg.getEntityId();
        this.holdingId = arg2 != null ? arg2.getEntityId() : 0;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.attachedId = arg.readInt();
        this.holdingId = arg.readInt();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeInt(this.attachedId);
        arg.writeInt(this.holdingId);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityAttach(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getAttachedEntityId() {
        return this.attachedId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getHoldingEntityId() {
        return this.holdingId;
    }
}

