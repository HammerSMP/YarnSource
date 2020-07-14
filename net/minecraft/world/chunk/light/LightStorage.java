/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.SectionDistanceLevelPropagator;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class LightStorage<M extends ChunkToNibbleArrayMap<M>>
extends SectionDistanceLevelPropagator {
    protected static final ChunkNibbleArray EMPTY = new ChunkNibbleArray();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightType lightType;
    private final ChunkProvider chunkProvider;
    protected final LongSet readySections = new LongOpenHashSet();
    protected final LongSet markedNotReadySections = new LongOpenHashSet();
    protected final LongSet markedReadySections = new LongOpenHashSet();
    protected volatile M uncachedStorage;
    protected final M storage;
    protected final LongSet dirtySections = new LongOpenHashSet();
    protected final LongSet notifySections = new LongOpenHashSet();
    protected final Long2ObjectMap<ChunkNibbleArray> queuedSections = Long2ObjectMaps.synchronize((Long2ObjectMap)new Long2ObjectOpenHashMap());
    private final LongSet queuedEdgeSections = new LongOpenHashSet();
    private final LongSet columnsToRetain = new LongOpenHashSet();
    private final LongSet sectionsToRemove = new LongOpenHashSet();
    protected volatile boolean hasLightUpdates;

    protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M lightData) {
        super(3, 16, 256);
        this.lightType = lightType;
        this.chunkProvider = chunkProvider;
        this.storage = lightData;
        this.uncachedStorage = ((ChunkToNibbleArrayMap)lightData).copy();
        ((ChunkToNibbleArrayMap)this.uncachedStorage).disableCache();
    }

    protected boolean hasSection(long sectionPos) {
        return this.getLightSection(sectionPos, true) != null;
    }

    @Nullable
    protected ChunkNibbleArray getLightSection(long sectionPos, boolean cached) {
        return this.getLightSection(cached ? this.storage : this.uncachedStorage, sectionPos);
    }

    @Nullable
    protected ChunkNibbleArray getLightSection(M storage, long sectionPos) {
        return ((ChunkToNibbleArrayMap)storage).get(sectionPos);
    }

    @Nullable
    public ChunkNibbleArray getLightSection(long sectionPos) {
        ChunkNibbleArray lv = (ChunkNibbleArray)this.queuedSections.get(sectionPos);
        if (lv != null) {
            return lv;
        }
        return this.getLightSection(sectionPos, false);
    }

    protected abstract int getLight(long var1);

    protected int get(long blockPos) {
        long m = ChunkSectionPos.fromBlockPos(blockPos);
        ChunkNibbleArray lv = this.getLightSection(m, true);
        return lv.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    protected void set(long blockPos, int value) {
        long m = ChunkSectionPos.fromBlockPos(blockPos);
        if (this.dirtySections.add(m)) {
            ((ChunkToNibbleArrayMap)this.storage).replaceWithCopy(m);
        }
        ChunkNibbleArray lv = this.getLightSection(m, true);
        lv.set(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)), value);
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int n = -1; n <= 1; ++n) {
                    this.notifySections.add(ChunkSectionPos.fromBlockPos(BlockPos.add(blockPos, k, n, j)));
                }
            }
        }
    }

    @Override
    protected int getLevel(long id) {
        if (id == Long.MAX_VALUE) {
            return 2;
        }
        if (this.readySections.contains(id)) {
            return 0;
        }
        if (!this.sectionsToRemove.contains(id) && ((ChunkToNibbleArrayMap)this.storage).containsKey(id)) {
            return 1;
        }
        return 2;
    }

    @Override
    protected int getInitialLevel(long id) {
        if (this.markedNotReadySections.contains(id)) {
            return 2;
        }
        if (this.readySections.contains(id) || this.markedReadySections.contains(id)) {
            return 0;
        }
        return 2;
    }

    @Override
    protected void setLevel(long id, int level) {
        int j = this.getLevel(id);
        if (j != 0 && level == 0) {
            this.readySections.add(id);
            this.markedReadySections.remove(id);
        }
        if (j == 0 && level != 0) {
            this.readySections.remove(id);
            this.markedNotReadySections.remove(id);
        }
        if (j >= 2 && level != 2) {
            if (this.sectionsToRemove.contains(id)) {
                this.sectionsToRemove.remove(id);
            } else {
                ((ChunkToNibbleArrayMap)this.storage).put(id, this.createSection(id));
                this.dirtySections.add(id);
                this.onLoadSection(id);
                for (int k = -1; k <= 1; ++k) {
                    for (int m = -1; m <= 1; ++m) {
                        for (int n = -1; n <= 1; ++n) {
                            this.notifySections.add(ChunkSectionPos.fromBlockPos(BlockPos.add(id, m, n, k)));
                        }
                    }
                }
            }
        }
        if (j != 2 && level >= 2) {
            this.sectionsToRemove.add(id);
        }
        this.hasLightUpdates = !this.sectionsToRemove.isEmpty();
    }

    protected ChunkNibbleArray createSection(long sectionPos) {
        ChunkNibbleArray lv = (ChunkNibbleArray)this.queuedSections.get(sectionPos);
        if (lv != null) {
            return lv;
        }
        return new ChunkNibbleArray();
    }

    protected void removeSection(ChunkLightProvider<?, ?> storage, long sectionPos) {
        if (storage.getPendingUpdateCount() < 8192) {
            storage.removePendingUpdateIf(m -> ChunkSectionPos.fromBlockPos(m) == sectionPos);
            return;
        }
        int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(sectionPos));
        int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(sectionPos));
        int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(sectionPos));
        for (int m2 = 0; m2 < 16; ++m2) {
            for (int n = 0; n < 16; ++n) {
                for (int o = 0; o < 16; ++o) {
                    long p = BlockPos.asLong(i + m2, j + n, k + o);
                    storage.removePendingUpdate(p);
                }
            }
        }
    }

    protected boolean hasLightUpdates() {
        return this.hasLightUpdates;
    }

    protected void updateLight(ChunkLightProvider<M, ?> lightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
        if (!this.hasLightUpdates() && this.queuedSections.isEmpty()) {
            return;
        }
        LongIterator longIterator = this.sectionsToRemove.iterator();
        while (longIterator.hasNext()) {
            long l = (Long)longIterator.next();
            this.removeSection(lightProvider, l);
            ChunkNibbleArray lv = (ChunkNibbleArray)this.queuedSections.remove(l);
            ChunkNibbleArray lv2 = ((ChunkToNibbleArrayMap)this.storage).removeChunk(l);
            if (!this.columnsToRetain.contains(ChunkSectionPos.withZeroZ(l))) continue;
            if (lv != null) {
                this.queuedSections.put(l, (Object)lv);
                continue;
            }
            if (lv2 == null) continue;
            this.queuedSections.put(l, (Object)lv2);
        }
        ((ChunkToNibbleArrayMap)this.storage).clearCache();
        longIterator = this.sectionsToRemove.iterator();
        while (longIterator.hasNext()) {
            long m = (Long)longIterator.next();
            this.onUnloadSection(m);
        }
        this.sectionsToRemove.clear();
        this.hasLightUpdates = false;
        for (Long2ObjectMap.Entry entry : this.queuedSections.long2ObjectEntrySet()) {
            long n = entry.getLongKey();
            if (!this.hasSection(n)) continue;
            ChunkNibbleArray lv3 = (ChunkNibbleArray)entry.getValue();
            if (((ChunkToNibbleArrayMap)this.storage).get(n) == lv3) continue;
            this.removeSection(lightProvider, n);
            ((ChunkToNibbleArrayMap)this.storage).put(n, lv3);
            this.dirtySections.add(n);
        }
        ((ChunkToNibbleArrayMap)this.storage).clearCache();
        if (!skipEdgeLightPropagation) {
            longIterator = this.queuedSections.keySet().iterator();
            while (longIterator.hasNext()) {
                long o = (Long)longIterator.next();
                this.updateSection(lightProvider, o);
            }
        } else {
            longIterator = this.queuedEdgeSections.iterator();
            while (longIterator.hasNext()) {
                long p = (Long)longIterator.next();
                this.updateSection(lightProvider, p);
            }
        }
        this.queuedEdgeSections.clear();
        ObjectIterator objectIterator = this.queuedSections.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry2 = (Long2ObjectMap.Entry)objectIterator.next();
            long q = entry2.getLongKey();
            if (!this.hasSection(q)) continue;
            objectIterator.remove();
        }
    }

    private void updateSection(ChunkLightProvider<M, ?> lightProvider, long sectionPos) {
        if (!this.hasSection(sectionPos)) {
            return;
        }
        int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(sectionPos));
        int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(sectionPos));
        int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(sectionPos));
        for (Direction lv : DIRECTIONS) {
            long m = ChunkSectionPos.offset(sectionPos, lv);
            if (this.queuedSections.containsKey(m) || !this.hasSection(m)) continue;
            for (int n = 0; n < 16; ++n) {
                for (int o = 0; o < 16; ++o) {
                    long aa;
                    long z;
                    switch (lv) {
                        case DOWN: {
                            long p = BlockPos.asLong(i + o, j, k + n);
                            long q = BlockPos.asLong(i + o, j - 1, k + n);
                            break;
                        }
                        case UP: {
                            long r = BlockPos.asLong(i + o, j + 16 - 1, k + n);
                            long s = BlockPos.asLong(i + o, j + 16, k + n);
                            break;
                        }
                        case NORTH: {
                            long t = BlockPos.asLong(i + n, j + o, k);
                            long u = BlockPos.asLong(i + n, j + o, k - 1);
                            break;
                        }
                        case SOUTH: {
                            long v = BlockPos.asLong(i + n, j + o, k + 16 - 1);
                            long w = BlockPos.asLong(i + n, j + o, k + 16);
                            break;
                        }
                        case WEST: {
                            long x = BlockPos.asLong(i, j + n, k + o);
                            long y = BlockPos.asLong(i - 1, j + n, k + o);
                            break;
                        }
                        default: {
                            z = BlockPos.asLong(i + 16 - 1, j + n, k + o);
                            aa = BlockPos.asLong(i + 16, j + n, k + o);
                        }
                    }
                    lightProvider.updateLevel(z, aa, lightProvider.getPropagatedLevel(z, aa, lightProvider.getLevel(z)), false);
                    lightProvider.updateLevel(aa, z, lightProvider.getPropagatedLevel(aa, z, lightProvider.getLevel(aa)), false);
                }
            }
        }
    }

    protected void onLoadSection(long sectionPos) {
    }

    protected void onUnloadSection(long sectionPos) {
    }

    protected void setColumnEnabled(long columnPos, boolean enabled) {
    }

    public void setRetainColumn(long sectionPos, boolean retain) {
        if (retain) {
            this.columnsToRetain.add(sectionPos);
        } else {
            this.columnsToRetain.remove(sectionPos);
        }
    }

    protected void enqueueSectionData(long sectionPos, @Nullable ChunkNibbleArray array, boolean bl) {
        if (array != null) {
            this.queuedSections.put(sectionPos, (Object)array);
            if (!bl) {
                this.queuedEdgeSections.add(sectionPos);
            }
        } else {
            this.queuedSections.remove(sectionPos);
        }
    }

    protected void setSectionStatus(long sectionPos, boolean notReady) {
        boolean bl2 = this.readySections.contains(sectionPos);
        if (!bl2 && !notReady) {
            this.markedReadySections.add(sectionPos);
            this.updateLevel(Long.MAX_VALUE, sectionPos, 0, true);
        }
        if (bl2 && notReady) {
            this.markedNotReadySections.add(sectionPos);
            this.updateLevel(Long.MAX_VALUE, sectionPos, 2, false);
        }
    }

    protected void updateAll() {
        if (this.hasPendingUpdates()) {
            this.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }

    protected void notifyChanges() {
        if (!this.dirtySections.isEmpty()) {
            Object lv = ((ChunkToNibbleArrayMap)this.storage).copy();
            ((ChunkToNibbleArrayMap)lv).disableCache();
            this.uncachedStorage = lv;
            this.dirtySections.clear();
        }
        if (!this.notifySections.isEmpty()) {
            LongIterator longIterator = this.notifySections.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
            }
            this.notifySections.clear();
        }
    }
}

