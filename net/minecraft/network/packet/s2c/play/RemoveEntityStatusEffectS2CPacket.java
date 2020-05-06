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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;

public class RemoveEntityStatusEffectS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entityId;
    private StatusEffect effectType;

    public RemoveEntityStatusEffectS2CPacket() {
    }

    public RemoveEntityStatusEffectS2CPacket(int i, StatusEffect arg) {
        this.entityId = i;
        this.effectType = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityId = arg.readVarInt();
        this.effectType = StatusEffect.byRawId(arg.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityId);
        arg.writeByte(StatusEffect.getRawId(this.effectType));
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onRemoveEntityEffect(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World arg) {
        return arg.getEntityById(this.entityId);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StatusEffect getEffectType() {
        return this.effectType;
    }
}

