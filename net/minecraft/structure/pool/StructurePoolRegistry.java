/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;

public class StructurePoolRegistry {
    private final Map<Identifier, StructurePool> pools = Maps.newHashMap();

    public StructurePoolRegistry() {
        this.add(StructurePool.EMPTY);
    }

    public void add(StructurePool arg) {
        this.pools.put(arg.getId(), arg);
    }

    public StructurePool get(Identifier arg) {
        StructurePool lv = this.pools.get(arg);
        return lv != null ? lv : StructurePool.INVALID;
    }
}

