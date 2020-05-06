/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.stat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;

public class StatHandler {
    protected final Object2IntMap<Stat<?>> statMap = Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap());

    public StatHandler() {
        this.statMap.defaultReturnValue(0);
    }

    public void increaseStat(PlayerEntity arg, Stat<?> arg2, int i) {
        int j = (int)Math.min((long)this.getStat(arg2) + (long)i, Integer.MAX_VALUE);
        this.setStat(arg, arg2, j);
    }

    public void setStat(PlayerEntity arg, Stat<?> arg2, int i) {
        this.statMap.put(arg2, i);
    }

    @Environment(value=EnvType.CLIENT)
    public <T> int getStat(StatType<T> arg, T object) {
        return arg.hasStat(object) ? this.getStat(arg.getOrCreateStat(object)) : 0;
    }

    public int getStat(Stat<?> arg) {
        return this.statMap.getInt(arg);
    }
}

