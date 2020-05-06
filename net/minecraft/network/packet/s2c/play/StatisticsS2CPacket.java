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

    public StatisticsS2CPacket(Object2IntMap<Stat<?>> object2IntMap) {
        this.stats = object2IntMap;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onStatistics(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        int i = arg.readVarInt();
        this.stats = new Object2IntOpenHashMap(i);
        for (int j = 0; j < i; ++j) {
            this.readStat((StatType)Registry.STAT_TYPE.get(arg.readVarInt()), arg);
        }
    }

    private <T> void readStat(StatType<T> arg, PacketByteBuf arg2) {
        int i = arg2.readVarInt();
        int j = arg2.readVarInt();
        this.stats.put(arg.getOrCreateStat(arg.getRegistry().get(i)), j);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.stats.size());
        for (Object2IntMap.Entry entry : this.stats.object2IntEntrySet()) {
            Stat lv = (Stat)entry.getKey();
            arg.writeVarInt(Registry.STAT_TYPE.getRawId(lv.getType()));
            arg.writeVarInt(this.getStatId(lv));
            arg.writeVarInt(entry.getIntValue());
        }
    }

    private <T> int getStatId(Stat<T> arg) {
        return arg.getType().getRegistry().getRawId(arg.getValue());
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Stat<?>, Integer> getStatMap() {
        return this.stats;
    }
}

