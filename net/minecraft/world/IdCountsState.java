/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

public class IdCountsState
extends PersistentState {
    private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap();

    public IdCountsState() {
        super("idcounts");
        this.idCounts.defaultReturnValue(-1);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.idCounts.clear();
        for (String string : tag.getKeys()) {
            if (!tag.contains(string, 99)) continue;
            this.idCounts.put((Object)string, tag.getInt(string));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        for (Object2IntMap.Entry entry : this.idCounts.object2IntEntrySet()) {
            tag.putInt((String)entry.getKey(), entry.getIntValue());
        }
        return tag;
    }

    public int getNextMapId() {
        int i = this.idCounts.getInt((Object)"map") + 1;
        this.idCounts.put((Object)"map", i);
        this.markDirty();
        return i;
    }
}

