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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityStatusEffectS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entityId;
    private byte effectId;
    private byte amplifier;
    private int duration;
    private byte flags;

    public EntityStatusEffectS2CPacket() {
    }

    public EntityStatusEffectS2CPacket(int i, StatusEffectInstance arg) {
        this.entityId = i;
        this.effectId = (byte)(StatusEffect.getRawId(arg.getEffectType()) & 0xFF);
        this.amplifier = (byte)(arg.getAmplifier() & 0xFF);
        this.duration = arg.getDuration() > 32767 ? 32767 : arg.getDuration();
        this.flags = 0;
        if (arg.isAmbient()) {
            this.flags = (byte)(this.flags | 1);
        }
        if (arg.shouldShowParticles()) {
            this.flags = (byte)(this.flags | 2);
        }
        if (arg.shouldShowIcon()) {
            this.flags = (byte)(this.flags | 4);
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.entityId = arg.readVarInt();
        this.effectId = arg.readByte();
        this.amplifier = arg.readByte();
        this.duration = arg.readVarInt();
        this.flags = arg.readByte();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.entityId);
        arg.writeByte(this.effectId);
        arg.writeByte(this.amplifier);
        arg.writeVarInt(this.duration);
        arg.writeByte(this.flags);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isPermanent() {
        return this.duration == 32767;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityPotionEffect(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getEffectId() {
        return this.effectId;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getAmplifier() {
        return this.amplifier;
    }

    @Environment(value=EnvType.CLIENT)
    public int getDuration() {
        return this.duration;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowParticles() {
        return (this.flags & 2) == 2;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isAmbient() {
        return (this.flags & 1) == 1;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowIcon() {
        return (this.flags & 4) == 4;
    }
}

