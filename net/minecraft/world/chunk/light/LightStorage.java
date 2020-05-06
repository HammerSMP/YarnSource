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
    protected final LongSet nonEmptySections = new LongOpenHashSet();
    protected final LongSet field_15797 = new LongOpenHashSet();
    protected final LongSet field_15804 = new LongOpenHashSet();
    protected volatile M uncachedLightArrays;
    protected final M lightArrays;
    protected final LongSet field_15802 = new LongOpenHashSet();
    protected final LongSet dirtySections = new LongOpenHashSet();
    protected final Long2ObjectMap<ChunkNibbleArray> lightArraysToAdd = Long2ObjectMaps.synchronize((Long2ObjectMap)new Long2ObjectOpenHashMap());
    private final LongSet field_19342 = new LongOpenHashSet();
    private final LongSet lightArraysToRemove = new LongOpenHashSet();
    protected volatile boolean hasLightUpdates;

    protected LightStorage(LightType arg, ChunkProvider arg2, M arg3) {
        super(3, 16, 256);
        this.lightType = arg;
        this.chunkProvider = arg2;
        this.lightArrays = arg3;
        this.uncachedLightArrays = ((ChunkToNibbleArrayMap)arg3).copy();
        ((ChunkToNibbleArrayMap)this.uncachedLightArrays).disableCache();
    }

    protected boolean hasLight(long l) {
        return this.getLightArray(l, true) != null;
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(long l, boolean bl) {
        return this.getLightArray(bl ? this.lightArrays : this.uncachedLightArrays, l);
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(M arg, long l) {
        return ((ChunkToNibbleArrayMap)arg).get(l);
    }

    @Nullable
    public ChunkNibbleArray getLightArray(long l) {
        ChunkNibbleArray lv = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
        if (lv != null) {
            return lv;
        }
        return this.getLightArray(l, false);
    }

    protected abstract int getLight(long var1);

    protected int get(long l) {
        long m = ChunkSectionPos.fromGlobalPos(l);
        ChunkNibbleArray lv = this.getLightArray(m, true);
        return lv.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)));
    }

    protected void set(long l, int i) {
        long m = ChunkSectionPos.fromGlobalPos(l);
        if (this.field_15802.add(m)) {
            ((ChunkToNibbleArrayMap)this.lightArrays).replaceWithCopy(m);
        }
        ChunkNibbleArray lv = this.getLightArray(m, true);
        lv.set(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)), i);
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int n = -1; n <= 1; ++n) {
                    this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(l, k, n, j)));
                }
            }
        }
    }

    @Override
    protected int getLevel(long l) {
        if (l == Long.MAX_VALUE) {
            return 2;
        }
        if (this.nonEmptySections.contains(l)) {
            return 0;
        }
        if (!this.lightArraysToRemove.contains(l) && ((ChunkToNibbleArrayMap)this.lightArrays).containsKey(l)) {
            return 1;
        }
        return 2;
    }

    @Override
    protected int getInitialLevel(long l) {
        if (this.field_15797.contains(l)) {
            return 2;
        }
        if (this.nonEmptySections.contains(l) || this.field_15804.contains(l)) {
            return 0;
        }
        return 2;
    }

    @Override
    protected void setLevel(long l, int i) {
        int j = this.getLevel(l);
        if (j != 0 && i == 0) {
            this.nonEmptySections.add(l);
            this.field_15804.remove(l);
        }
        if (j == 0 && i != 0) {
            this.nonEmptySections.remove(l);
            this.field_15797.remove(l);
        }
        if (j >= 2 && i != 2) {
            if (this.lightArraysToRemove.contains(l)) {
                this.lightArraysToRemove.remove(l);
            } else {
                ((ChunkToNibbleArrayMap)this.lightArrays).put(l, this.createLightArray(l));
                this.field_15802.add(l);
                this.onLightArrayCreated(l);
                for (int k = -1; k <= 1; ++k) {
                    for (int m = -1; m <= 1; ++m) {
                        for (int n = -1; n <= 1; ++n) {
                            this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(l, m, n, k)));
                        }
                    }
                }
            }
        }
        if (j != 2 && i >= 2) {
            this.lightArraysToRemove.add(l);
        }
        this.hasLightUpdates = !this.lightArraysToRemove.isEmpty();
    }

    protected ChunkNibbleArray createLightArray(long l) {
        ChunkNibbleArray lv = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
        if (lv != null) {
            return lv;
        }
        return new ChunkNibbleArray();
    }

    protected void removeChunkData(ChunkLightProvider<?, ?> arg, long l) {
        if (arg.method_24208() < 8192) {
            arg.method_24206(m -> ChunkSectionPos.fromGlobalPos(m) == l);
            return;
        }
        int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
        int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
        int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));
        for (int m2 = 0; m2 < 16; ++m2) {
            for (int n = 0; n < 16; ++n) {
                for (int o = 0; o < 16; ++o) {
                    long p = BlockPos.asLong(i + m2, j + n, k + o);
                    arg.removePendingUpdate(p);
                }
            }
        }
    }

    protected boolean hasLightUpdates() {
        return this.hasLightUpdates;
    }

    protected void updateLightArrays(ChunkLightProvider<M, ?> arg, boolean bl, boolean bl2) {
        if (!this.hasLightUpdates() && this.lightArraysToAdd.isEmpty()) {
            return;
        }
        LongIterator longIterator = this.lightArraysToRemove.iterator();
        while (longIterator.hasNext()) {
            long l = (Long)longIterator.next();
            this.removeChunkData(arg, l);
            ChunkNibbleArray lv = (ChunkNibbleArray)this.lightArraysToAdd.remove(l);
            ChunkNibbleArray lv2 = ((ChunkToNibbleArrayMap)this.lightArrays).removeChunk(l);
            if (!this.field_19342.contains(ChunkSectionPos.withZeroZ(l))) continue;
            if (lv != null) {
                this.lightArraysToAdd.put(l, (Object)lv);
                continue;
            }
            if (lv2 == null) continue;
            this.lightArraysToAdd.put(l, (Object)lv2);
        }
        ((ChunkToNibbleArrayMap)this.lightArrays).clearCache();
        longIterator = this.lightArraysToRemove.iterator();
        while (longIterator.hasNext()) {
            long m = (Long)longIterator.next();
            this.onChunkRemoved(m);
        }
        this.lightArraysToRemove.clear();
        this.hasLightUpdates = false;
        for (Long2ObjectMap.Entry entry : this.lightArraysToAdd.long2ObjectEntrySet()) {
            long n = entry.getLongKey();
            if (!this.hasLight(n)) continue;
            ChunkNibbleArray lv3 = (ChunkNibbleArray)entry.getValue();
            if (((ChunkToNibbleArrayMap)this.lightArrays).get(n) == lv3) continue;
            this.removeChunkData(arg, n);
            ((ChunkToNibbleArrayMap)this.lightArrays).put(n, lv3);
            this.field_15802.add(n);
        }
        ((ChunkToNibbleArrayMap)this.lightArrays).clearCache();
        if (!bl2) {
            longIterator = this.lightArraysToAdd.keySet().iterator();
            while (longIterator.hasNext()) {
                long o = (Long)longIterator.next();
                if (!this.hasLight(o)) continue;
                int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(o));
                int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(o));
                int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(o));
                for (Direction lv4 : DIRECTIONS) {
                    long p = ChunkSectionPos.offset(o, lv4);
                    if (this.lightArraysToAdd.containsKey(p) || !this.hasLight(p)) continue;
                    for (int q = 0; q < 16; ++q) {
                        for (int r = 0; r < 16; ++r) {
                            long ad;
                            long ac;
                            switch (lv4) {
                                case DOWN: {
                                    long s = BlockPos.asLong(i + r, j, k + q);
                                    long t = BlockPos.asLong(i + r, j - 1, k + q);
                                    break;
                                }
                                case UP: {
                                    long u = BlockPos.asLong(i + r, j + 16 - 1, k + q);
                                    long v = BlockPos.asLong(i + r, j + 16, k + q);
                                    break;
                                }
                                case NORTH: {
                                    long w = BlockPos.asLong(i + q, j + r, k);
                                    long x = BlockPos.asLong(i + q, j + r, k - 1);
                                    break;
                                }
                                case SOUTH: {
                                    long y = BlockPos.asLong(i + q, j + r, k + 16 - 1);
                                    long z = BlockPos.asLong(i + q, j + r, k + 16);
                                    break;
                                }
                                case WEST: {
                                    long aa = BlockPos.asLong(i, j + q, k + r);
                                    long ab = BlockPos.asLong(i - 1, j + q, k + r);
                                    break;
                                }
                                default: {
                                    ac = BlockPos.asLong(i + 16 - 1, j + q, k + r);
                                    ad = BlockPos.asLong(i + 16, j + q, k + r);
                                }
                            }
                            arg.updateLevel(ac, ad, arg.getPropagatedLevel(ac, ad, arg.getLevel(ac)), false);
                            arg.updateLevel(ad, ac, arg.getPropagatedLevel(ad, ac, arg.getLevel(ad)), false);
                        }
                    }
                }
            }
        }
        ObjectIterator objectIterator = this.lightArraysToAdd.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry2 = (Long2ObjectMap.Entry)objectIterator.next();
            long ae = entry2.getLongKey();
            if (!this.hasLight(ae)) continue;
            objectIterator.remove();
        }
    }

    protected void onLightArrayCreated(long l) {
    }

    protected void onChunkRemoved(long l) {
    }

    protected void setLightEnabled(long l, boolean bl) {
    }

    public void setRetainData(long l, boolean bl) {
        if (bl) {
            this.field_19342.add(l);
        } else {
            this.field_19342.remove(l);
        }
    }

    protected void setLightArray(long l, @Nullable ChunkNibbleArray arg) {
        if (arg != null) {
            this.lightArraysToAdd.put(l, (Object)arg);
        } else {
            this.lightArraysToAdd.remove(l);
        }
    }

    protected void updateSectionStatus(long l, boolean bl) {
        boolean bl2 = this.nonEmptySections.contains(l);
        if (!bl2 && !bl) {
            this.field_15804.add(l);
            this.updateLevel(Long.MAX_VALUE, l, 0, true);
        }
        if (bl2 && bl) {
            this.field_15797.add(l);
            this.updateLevel(Long.MAX_VALUE, l, 2, false);
        }
    }

    protected void updateAll() {
        if (this.hasPendingUpdates()) {
            this.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }

    protected void notifyChunkProvider() {
        if (!this.field_15802.isEmpty()) {
            Object lv = ((ChunkToNibbleArrayMap)this.lightArrays).copy();
            ((ChunkToNibbleArrayMap)lv).disableCache();
            this.uncachedLightArrays = lv;
            this.field_15802.clear();
        }
        if (!this.dirtySections.isEmpty()) {
            LongIterator longIterator = this.dirtySections.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
            }
            this.dirtySections.clear();
        }
    }
}

