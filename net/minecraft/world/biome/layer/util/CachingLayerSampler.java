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

    public CachingLayerSampler(Long2IntLinkedOpenHashMap cache, int cacheCapacity, LayerOperator operator) {
        this.cache = cache;
        this.cacheCapacity = cacheCapacity;
        this.operator = operator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sample(int x, int z) {
        long l = ChunkPos.toLong(x, z);
        Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = this.cache;
        synchronized (long2IntLinkedOpenHashMap) {
            int k = this.cache.get(l);
            if (k != Integer.MIN_VALUE) {
                return k;
            }
            int m = this.operator.apply(x, z);
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

