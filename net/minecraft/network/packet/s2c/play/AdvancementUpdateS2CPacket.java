/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public class AdvancementUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private boolean clearCurrent;
    private Map<Identifier, Advancement.Task> toEarn;
    private Set<Identifier> toRemove;
    private Map<Identifier, AdvancementProgress> toSetProgress;

    public AdvancementUpdateS2CPacket() {
    }

    public AdvancementUpdateS2CPacket(boolean bl, Collection<Advancement> collection, Set<Identifier> set, Map<Identifier, AdvancementProgress> map) {
        this.clearCurrent = bl;
        this.toEarn = Maps.newHashMap();
        for (Advancement lv : collection) {
            this.toEarn.put(lv.getId(), lv.createTask());
        }
        this.toRemove = set;
        this.toSetProgress = Maps.newHashMap(map);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onAdvancements(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.clearCurrent = arg.readBoolean();
        this.toEarn = Maps.newHashMap();
        this.toRemove = Sets.newLinkedHashSet();
        this.toSetProgress = Maps.newHashMap();
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            Identifier lv = arg.readIdentifier();
            Advancement.Task lv2 = Advancement.Task.fromPacket(arg);
            this.toEarn.put(lv, lv2);
        }
        i = arg.readVarInt();
        for (int k = 0; k < i; ++k) {
            Identifier lv3 = arg.readIdentifier();
            this.toRemove.add(lv3);
        }
        i = arg.readVarInt();
        for (int l = 0; l < i; ++l) {
            Identifier lv4 = arg.readIdentifier();
            this.toSetProgress.put(lv4, AdvancementProgress.fromPacket(arg));
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeBoolean(this.clearCurrent);
        arg.writeVarInt(this.toEarn.size());
        for (Map.Entry<Identifier, Advancement.Task> entry : this.toEarn.entrySet()) {
            Identifier lv = entry.getKey();
            Advancement.Task lv2 = entry.getValue();
            arg.writeIdentifier(lv);
            lv2.toPacket(arg);
        }
        arg.writeVarInt(this.toRemove.size());
        for (Identifier identifier : this.toRemove) {
            arg.writeIdentifier(identifier);
        }
        arg.writeVarInt(this.toSetProgress.size());
        for (Map.Entry entry : this.toSetProgress.entrySet()) {
            arg.writeIdentifier((Identifier)entry.getKey());
            ((AdvancementProgress)entry.getValue()).toPacket(arg);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Identifier, Advancement.Task> getAdvancementsToEarn() {
        return this.toEarn;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<Identifier> getAdvancementIdsToRemove() {
        return this.toRemove;
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Identifier, AdvancementProgress> getAdvancementsToProgress() {
        return this.toSetProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldClearCurrent() {
        return this.clearCurrent;
    }
}

