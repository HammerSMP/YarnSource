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
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityAnimationS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private int animationId;

    public EntityAnimationS2CPacket() {
    }

    public EntityAnimationS2CPacket(Entity arg, int i) {
        this.id = arg.getEntityId();
        this.animationId = i;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.animationId = arg.readUnsignedByte();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeByte(this.animationId);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityAnimation(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public int getAnimationId() {
        return this.animationId;
    }
}

