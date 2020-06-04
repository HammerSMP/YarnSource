/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.ColumnChunkNibbleArray;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightStorage;

public class SkyLightStorage
extends LightStorage<Data> {
    private static final Direction[] LIGHT_REDUCTION_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet field_15820 = new LongOpenHashSet();
    private final LongSet pendingSkylightUpdates = new LongOpenHashSet();
    private final LongSet field_15816 = new LongOpenHashSet();
    private final LongSet lightEnabled = new LongOpenHashSet();
    private volatile boolean hasSkyLightUpdates;

    protected SkyLightStorage(ChunkProvider arg) {
        super(LightType.SKY, arg, new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLight(long l) {
        long m = ChunkSectionPos.fromGlobalPos(l);
        int i = ChunkSectionPos.getY(m);
        Data lv = (Data)this.uncachedLightArrays;
        int j = lv.topArraySectionY.get(ChunkSectionPos.withZeroZ(m));
        if (j == lv.defaultTopArraySectionY || i >= j) {
            return 15;
        }
        ChunkNibbleArray lv2 = this.getLightArray(lv, m);
        if (lv2 == null) {
            l = BlockPos.removeChunkSectionLocalY(l);
            while (lv2 == null) {
                m = ChunkSectionPos.offset(m, Direction.UP);
                if (++i >= j) {
                    return 15;
                }
                l = BlockPos.add(l, 0, 16, 0);
                lv2 = this.getLightArray(lv, m);
            }
        }
        return lv2.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)));
    }

    @Override
    protected void onLightArrayCreated(long l) {
        int i = ChunkSectionPos.getY(l);
        if (((Data)this.lightArrays).defaultTopArraySectionY > i) {
            ((Data)this.lightArrays).defaultTopArraySectionY = i;
            ((Data)this.lightArrays).topArraySectionY.defaultReturnValue(((Data)this.lightArrays).defaultTopArraySectionY);
        }
        long m = ChunkSectionPos.withZeroZ(l);
        int j = ((Data)this.lightArrays).topArraySectionY.get(m);
        if (j < i + 1) {
            ((Data)this.lightArrays).topArraySectionY.put(m, i + 1);
            if (this.lightEnabled.contains(m)) {
                this.method_20810(l);
                if (j > ((Data)this.lightArrays).defaultTopArraySectionY) {
                    long n = ChunkSectionPos.asLong(ChunkSectionPos.getX(l), j - 1, ChunkSectionPos.getZ(l));
                    this.method_20809(n);
                }
                this.checkForUpdates();
            }
        }
    }

    private void method_20809(long l) {
        this.field_15816.add(l);
        this.pendingSkylightUpdates.remove(l);
    }

    private void method_20810(long l) {
        this.pendingSkylightUpdates.add(l);
        this.field_15816.remove(l);
    }

    private void checkForUpdates() {
        this.hasSkyLightUpdates = !this.pendingSkylightUpdates.isEmpty() || !this.field_15816.isEmpty();
    }

    @Override
    protected void onChunkRemoved(long l) {
        long m = ChunkSectionPos.withZeroZ(l);
        boolean bl = this.lightEnabled.contains(m);
        if (bl) {
            this.method_20809(l);
        }
        int i = ChunkSectionPos.getY(l);
        if (((Data)this.lightArrays).topArraySectionY.get(m) == i + 1) {
            long n = l;
            while (!this.hasLight(n) && this.isAboveMinHeight(i)) {
                --i;
                n = ChunkSectionPos.offset(n, Direction.DOWN);
            }
            if (this.hasLight(n)) {
                ((Data)this.lightArrays).topArraySectionY.put(m, i + 1);
                if (bl) {
                    this.method_20810(n);
                }
            } else {
                ((Data)this.lightArrays).topArraySectionY.remove(m);
            }
        }
        if (bl) {
            this.checkForUpdates();
        }
    }

    @Override
    protected void setLightEnabled(long l, boolean bl) {
        this.updateAll();
        if (bl && this.lightEnabled.add(l)) {
            int i = ((Data)this.lightArrays).topArraySectionY.get(l);
            if (i != ((Data)this.lightArrays).defaultTopArraySectionY) {
                long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(l), i - 1, ChunkSectionPos.getZ(l));
                this.method_20810(m);
                this.checkForUpdates();
            }
        } else if (!bl) {
            this.lightEnabled.remove(l);
        }
    }

    @Override
    protected boolean hasLightUpdates() {
        return super.hasLightUpdates() || this.hasSkyLightUpdates;
    }

    @Override
    protected ChunkNibbleArray createLightArray(long l) {
        ChunkNibbleArray lv2;
        ChunkNibbleArray lv = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
        if (lv != null) {
            return lv;
        }
        long m = ChunkSectionPos.offset(l, Direction.UP);
        int i = ((Data)this.lightArrays).topArraySectionY.get(ChunkSectionPos.withZeroZ(l));
        if (i == ((Data)this.lightArrays).defaultTopArraySectionY || ChunkSectionPos.getY(m) >= i) {
            return new ChunkNibbleArray();
        }
        while ((lv2 = this.getLightArray(m, true)) == null) {
            m = ChunkSectionPos.offset(m, Direction.UP);
        }
        return new ChunkNibbleArray(new ColumnChunkNibbleArray(lv2, 0).asByteArray());
    }

    @Override
    protected void updateLightArrays(ChunkLightProvider<Data, ?> arg, boolean bl, boolean bl2) {
        LongIterator longIterator;
        super.updateLightArrays(arg, bl, bl2);
        if (!bl) {
            return;
        }
        if (!this.pendingSkylightUpdates.isEmpty()) {
            longIterator = this.pendingSkylightUpdates.iterator();
            while (longIterator.hasNext()) {
                long l = (Long)longIterator.next();
                int i = this.getLevel(l);
                if (i == 2 || this.field_15816.contains(l) || !this.field_15820.add(l)) continue;
                if (i == 1) {
                    this.removeChunkData(arg, l);
                    if (this.field_15802.add(l)) {
                        ((Data)this.lightArrays).replaceWithCopy(l);
                    }
                    Arrays.fill(this.getLightArray(l, true).asByteArray(), (byte)-1);
                    int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
                    int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
                    int m = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));
                    for (Direction lv : LIGHT_REDUCTION_DIRECTIONS) {
                        long n = ChunkSectionPos.offset(l, lv);
                        if (!this.field_15816.contains(n) && (this.field_15820.contains(n) || this.pendingSkylightUpdates.contains(n)) || !this.hasLight(n)) continue;
                        for (int o = 0; o < 16; ++o) {
                            for (int p = 0; p < 16; ++p) {
                                long x;
                                long w;
                                switch (lv) {
                                    case NORTH: {
                                        long q = BlockPos.asLong(j + o, k + p, m);
                                        long r = BlockPos.asLong(j + o, k + p, m - 1);
                                        break;
                                    }
                                    case SOUTH: {
                                        long s = BlockPos.asLong(j + o, k + p, m + 16 - 1);
                                        long t = BlockPos.asLong(j + o, k + p, m + 16);
                                        break;
                                    }
                                    case WEST: {
                                        long u = BlockPos.asLong(j, k + o, m + p);
                                        long v = BlockPos.asLong(j - 1, k + o, m + p);
                                        break;
                                    }
                                    default: {
                                        w = BlockPos.asLong(j + 16 - 1, k + o, m + p);
                                        x = BlockPos.asLong(j + 16, k + o, m + p);
                                    }
                                }
                                arg.updateLevel(w, x, arg.getPropagatedLevel(w, x, 0), true);
                            }
                        }
                    }
                    for (int y = 0; y < 16; ++y) {
                        for (int z = 0; z < 16; ++z) {
                            long aa = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + y, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)), ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + z);
                            long ab = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + y, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) - 1, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + z);
                            arg.updateLevel(aa, ab, arg.getPropagatedLevel(aa, ab, 0), true);
                        }
                    }
                    continue;
                }
                for (int ac = 0; ac < 16; ++ac) {
                    for (int ad = 0; ad < 16; ++ad) {
                        long ae = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + ac, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) + 16 - 1, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + ad);
                        arg.updateLevel(Long.MAX_VALUE, ae, 0, true);
                    }
                }
            }
        }
        this.pendingSkylightUpdates.clear();
        if (!this.field_15816.isEmpty()) {
            longIterator = this.field_15816.iterator();
            while (longIterator.hasNext()) {
                long af = (Long)longIterator.next();
                if (!this.field_15820.remove(af) || !this.hasLight(af)) continue;
                for (int ag = 0; ag < 16; ++ag) {
                    for (int ah = 0; ah < 16; ++ah) {
                        long ai = BlockPos.asLong(ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(af)) + ag, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(af)) + 16 - 1, ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(af)) + ah);
                        arg.updateLevel(Long.MAX_VALUE, ai, 15, false);
                    }
                }
            }
        }
        this.field_15816.clear();
        this.hasSkyLightUpdates = false;
    }

    protected boolean isAboveMinHeight(int i) {
        return i >= ((Data)this.lightArrays).defaultTopArraySectionY;
    }

    protected boolean method_15565(long l) {
        int i = BlockPos.unpackLongY(l);
        if ((i & 0xF) != 15) {
            return false;
        }
        long m = ChunkSectionPos.fromGlobalPos(l);
        long n = ChunkSectionPos.withZeroZ(m);
        if (!this.lightEnabled.contains(n)) {
            return false;
        }
        int j = ((Data)this.lightArrays).topArraySectionY.get(n);
        return ChunkSectionPos.getWorldCoord(j) == i + 16;
    }

    protected boolean isAboveTopmostLightArray(long l) {
        long m = ChunkSectionPos.withZeroZ(l);
        int i = ((Data)this.lightArrays).topArraySectionY.get(m);
        return i == ((Data)this.lightArrays).defaultTopArraySectionY || ChunkSectionPos.getY(l) >= i;
    }

    protected boolean isLightEnabled(long l) {
        long m = ChunkSectionPos.withZeroZ(l);
        return this.lightEnabled.contains(m);
    }

    public static final class Data
    extends ChunkToNibbleArrayMap<Data> {
        private int defaultTopArraySectionY;
        private final Long2IntOpenHashMap topArraySectionY;

        public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int i) {
            super(long2ObjectOpenHashMap);
            this.topArraySectionY = long2IntOpenHashMap;
            long2IntOpenHashMap.defaultReturnValue(i);
            this.defaultTopArraySectionY = i;
        }

        @Override
        public Data copy() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arrays.clone(), this.topArraySectionY.clone(), this.defaultTopArraySectionY);
        }

        @Override
        public /* synthetic */ ChunkToNibbleArrayMap copy() {
            return this.copy();
        }
    }
}

