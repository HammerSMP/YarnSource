/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityAttributesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entityId;
    private final List<Entry> entries = Lists.newArrayList();

    public EntityAttributesS2CPacket() {
    }

    public EntityAttributesS2CPacket(int entityId, Collection<EntityAttributeInstance> attributes) {
        this.entityId = entityId;
        for (EntityAttributeInstance lv : attributes) {
            this.entries.add(new Entry(lv.getAttribute(), lv.getBaseValue(), lv.getModifiers()));
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        int i = buf.readInt();
        for (int j = 0; j < i; ++j) {
            Identifier lv = buf.readIdentifier();
            EntityAttribute lv2 = Registry.ATTRIBUTE.get(lv);
            double d = buf.readDouble();
            ArrayList list = Lists.newArrayList();
            int k = buf.readVarInt();
            for (int l = 0; l < k; ++l) {
                UUID uUID = buf.readUuid();
                list.add(new EntityAttributeModifier(uUID, "Unknown synced attribute modifier", buf.readDouble(), EntityAttributeModifier.Operation.fromId(buf.readByte())));
            }
            this.entries.add(new Entry(lv2, d, list));
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeInt(this.entries.size());
        for (Entry lv : this.entries) {
            buf.writeIdentifier(Registry.ATTRIBUTE.getId(lv.getId()));
            buf.writeDouble(lv.getBaseValue());
            buf.writeVarInt(lv.getModifiers().size());
            for (EntityAttributeModifier lv2 : lv.getModifiers()) {
                buf.writeUuid(lv2.getId());
                buf.writeDouble(lv2.getValue());
                buf.writeByte(lv2.getOperation().getId());
            }
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEntityAttributes(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Entry> getEntries() {
        return this.entries;
    }

    public class Entry {
        private final EntityAttribute id;
        private final double baseValue;
        private final Collection<EntityAttributeModifier> modifiers;

        public Entry(EntityAttribute arg2, double d, Collection<EntityAttributeModifier> collection) {
            this.id = arg2;
            this.baseValue = d;
            this.modifiers = collection;
        }

        public EntityAttribute getId() {
            return this.id;
        }

        public double getBaseValue() {
            return this.baseValue;
        }

        public Collection<EntityAttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}

