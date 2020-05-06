/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class CombatEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    public Type type;
    public int entityId;
    public int attackerEntityId;
    public int timeSinceLastAttack;
    public Text deathMessage;

    public CombatEventS2CPacket() {
    }

    public CombatEventS2CPacket(DamageTracker arg, Type arg2) {
        this(arg, arg2, LiteralText.EMPTY);
    }

    public CombatEventS2CPacket(DamageTracker arg, Type arg2, Text arg3) {
        this.type = arg2;
        LivingEntity lv = arg.getBiggestAttacker();
        switch (arg2) {
            case END_COMBAT: {
                this.timeSinceLastAttack = arg.getTimeSinceLastAttack();
                this.attackerEntityId = lv == null ? -1 : lv.getEntityId();
                break;
            }
            case ENTITY_DIED: {
                this.entityId = arg.getEntity().getEntityId();
                this.attackerEntityId = lv == null ? -1 : lv.getEntityId();
                this.deathMessage = arg3;
            }
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.type = arg.readEnumConstant(Type.class);
        if (this.type == Type.END_COMBAT) {
            this.timeSinceLastAttack = arg.readVarInt();
            this.attackerEntityId = arg.readInt();
        } else if (this.type == Type.ENTITY_DIED) {
            this.entityId = arg.readVarInt();
            this.attackerEntityId = arg.readInt();
            this.deathMessage = arg.readText();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.type);
        if (this.type == Type.END_COMBAT) {
            arg.writeVarInt(this.timeSinceLastAttack);
            arg.writeInt(this.attackerEntityId);
        } else if (this.type == Type.ENTITY_DIED) {
            arg.writeVarInt(this.entityId);
            arg.writeInt(this.attackerEntityId);
            arg.writeText(this.deathMessage);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onCombatEvent(this);
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return this.type == Type.ENTITY_DIED;
    }

    public static enum Type {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DIED;

    }
}

