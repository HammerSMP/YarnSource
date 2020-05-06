/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 */
package net.minecraft.world.biome.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerSampler;

public final class CachingLayerSampler
implements LayerSampler {
    private final LayerOperator operator;
    private final Long2IntLinkedOpenHashMap cache;
    private final int cacheCapacity;

    public CachingLayerSampler(Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap, int i, LayerOperator arg) {
        this.cache = long2IntLinkedOpenHashMap;
        this.cacheCapacity = i;
        this.operator = arg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sample(int i, int j) {
        long l = ChunkPos.toLong(i, j);
        Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = this.cache;
        synchronized (long2IntLinkedOpenHashMap) {
            int k = this.cache.get(l);
            if (k != Integer.MIN_VALUE) {
                return k;
            }
            int m = this.operator.apply(i, j);
            this.cache.put(l, m);
            if (this.cache.size() > this.cacheCapacity) {
                for (int n = 0; n < this.cacheCapacity / 16; ++n) {
                    this.cache.removeFirstInt();
                }
            }
            return m;
        }
    }

    public int getCapacity() {
        return this.cacheCapacity;
    }
}

