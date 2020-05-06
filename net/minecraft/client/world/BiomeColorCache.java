/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

@Environment(value=EnvType.CLIENT)
public class BiomeColorCache {
    private final ThreadLocal<Last> last = ThreadLocal.withInitial(() -> new Last());
    private final Long2ObjectLinkedOpenHashMap<int[]> colors = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public int getBiomeColor(BlockPos arg, IntSupplier intSupplier) {
        int o;
        int i = arg.getX() >> 4;
        int j = arg.getZ() >> 4;
        Last lv = this.last.get();
        if (lv.x != i || lv.z != j) {
            lv.x = i;
            lv.z = j;
            lv.colors = this.getColorArray(i, j);
        }
        int k = arg.getX() & 0xF;
        int l = arg.getZ() & 0xF;
        int m = l << 4 | k;
        int n = lv.colors[m];
        if (n != -1) {
            return n;
        }
        lv.colors[m] = o = intSupplier.getAsInt();
        return o;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset(int i, int j) {
        try {
            this.lock.writeLock().lock();
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    long m = ChunkPos.toLong(i + k, j + l);
                    this.colors.remove(m);
                }
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void reset() {
        try {
            this.lock.writeLock().lock();
            this.colors.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private int[] getColorArray(int i, int j) {
        void js;
        long l = ChunkPos.toLong(i, j);
        this.lock.readLock().lock();
        try {
            int[] is = (int[])this.colors.get(l);
        }
        finally {
            this.lock.readLock().unlock();
        }
        if (js != null) {
            return js;
        }
        int[] ks = new int[256];
        Arrays.fill(ks, -1);
        try {
            this.lock.writeLock().lock();
            if (this.colors.size() >= 256) {
                this.colors.removeFirst();
            }
            this.colors.put(l, (Object)ks);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        return ks;
    }

    @Environment(value=EnvType.CLIENT)
    static class Last {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        public int[] colors;

        private Last() {
        }
    }
}

