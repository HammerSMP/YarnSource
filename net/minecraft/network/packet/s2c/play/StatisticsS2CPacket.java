/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.registry.Registry;

public class StatisticsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Object2IntMap<Stat<?>> stats;

    public StatisticsS2CPacket() {
    }

    public StatisticsS2CPacket(Object2IntMap<Stat<?>> stats) {
        this.stats = stats;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onStatistics(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        int i = buf.readVarInt();
        this.stats = new Object2IntOpenHashMap(i);
        for (int j = 0; j < i; ++j) {
            this.readStat((StatType)Registry.STAT_TYPE.get(buf.readVarInt()), buf);
        }
    }

    private <T> void readStat(StatType<T> type, PacketByteBuf buf) {
        int i = buf.readVarInt();
        int j = buf.readVarInt();
        this.stats.put(type.getOrCreateStat(type.getRegistry().get(i)), j);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.stats.size());
        for (Object2IntMap.Entry entry : this.stats.object2IntEntrySet()) {
            Stat lv = (Stat)entry.getKey();
            buf.writeVarInt(Registry.STAT_TYPE.getRawId(lv.getType()));
            buf.writeVarInt(this.getStatId(lv));
            buf.writeVarInt(entry.getIntValue());
        }
    }

    private <T> int getStatId(Stat<T> stat) {
        return stat.getType().getRegistry().getRawId(stat.getValue());
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Stat<?>, Integer> getStatMap() {
        return this.stats;
    }
}

