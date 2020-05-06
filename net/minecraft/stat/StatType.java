/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.registry.Registry;

public class StatType<T>
implements Iterable<Stat<T>> {
    private final Registry<T> registry;
    private final Map<T, Stat<T>> stats = new IdentityHashMap<T, Stat<T>>();

    public StatType(Registry<T> arg) {
        this.registry = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasStat(T object) {
        return this.stats.containsKey(object);
    }

    public Stat<T> getOrCreateStat(T object2, StatFormatter arg) {
        return this.stats.computeIfAbsent(object2, object -> new Stat<Object>(this, object, arg));
    }

    public Registry<T> getRegistry() {
        return this.registry;
    }

    @Override
    public Iterator<Stat<T>> iterator() {
        return this.stats.values().iterator();
    }

    public Stat<T> getOrCreateStat(T object) {
        return this.getOrCreateStat(object, StatFormatter.DEFAULT);
    }

    @Environment(value=EnvType.CLIENT)
    public String getTranslationKey() {
        return "stat_type." + Registry.STAT_TYPE.getId(this).toString().replace(':', '.');
    }
}

