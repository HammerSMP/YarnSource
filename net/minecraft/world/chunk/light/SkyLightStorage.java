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
    private final LongSet sectionsToUpdate = new LongOpenHashSet();
    private final LongSet sectionsToRemove = new LongOpenHashSet();
    private final LongSet enabledColumns = new LongOpenHashSet();
    private volatile boolean hasUpdates;

    protected SkyLightStorage(ChunkProvider chunkProvider) {
        super(LightType.SKY, chunkProvider, new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLight(long blockPos) {
        long m = ChunkSectionPos.fromBlockPos(blockPos);
        int i = ChunkSectionPos.getY(m);
        Data lv = (Data)this.uncachedStorage;
        int j = lv.columnToTopSection.get(ChunkSectionPos.withZeroZ(m));
        if (j == lv.minSectionY || i >= j) {
            return 15;
        }
        ChunkNibbleArray lv2 = this.getLightSection(lv, m);
        if (lv2 == null) {
            blockPos = BlockPos.removeChunkSectionLocalY(blockPos);
            while (lv2 == null) {
                m = ChunkSectionPos.offset(m, Direction.UP);
                if (++i >= j) {
                    return 15;
                }
                blockPos = BlockPos.add(blockPos, 0, 16, 0);
                lv2 = this.getLightSection(lv, m);
            }
        }
        return lv2.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    @Override
    protected void onLoadSection(long sectionPos) {
        int i = ChunkSectionPos.getY(sectionPos);
        if (((Data)this.storage).minSectionY > i) {
            ((Data)this.storage).minSectionY = i;
            ((Data)this.storage).columnToTopSection.defaultReturnValue(((Data)this.storage).minSectionY);
        }
        long m = ChunkSectionPos.withZeroZ(sectionPos);
        int j = ((Data)this.storage).columnToTopSection.get(m);
        if (j < i + 1) {
            ((Data)this.storage).columnToTopSection.put(m, i + 1);
            if (this.enabledColumns.contains(m)) {
                this.enqueueAddSection(sectionPos);
                if (j > ((Data)this.storage).minSectionY) {
                    long n = ChunkSectionPos.asLong(ChunkSectionPos.getX(sectionPos), j - 1, ChunkSectionPos.getZ(sectionPos));
                    this.enqueueRemoveSection(n);
                }
                this.checkForUpdates();
            }
        }
    }

    private void enqueueRemoveSection(long sectionPos) {
        this.sectionsToRemove.add(sectionPos);
        this.sectionsToUpdate.remove(sectionPos);
    }

    private void enqueueAddSection(long sectionPos) {
        this.sectionsToUpdate.add(sectionPos);
        this.sectionsToRemove.remove(sectionPos);
    }

    private void checkForUpdates() {
        this.hasUpdates = !this.sectionsToUpdate.isEmpty() || !this.sectionsToRemove.isEmpty();
    }

    @Override
    protected void onUnloadSection(long sectionPos) {
        long m = ChunkSectionPos.withZeroZ(sectionPos);
        boolean bl = this.enabledColumns.contains(m);
        if (bl) {
            this.enqueueRemoveSection(sectionPos);
        }
        int i = ChunkSectionPos.getY(sectionPos);
        if (((Data)this.storage).columnToTopSection.get(m) == i + 1) {
            long n = sectionPos;
            while (!this.hasSection(n) && this.isAboveMinHeight(i)) {
                --i;
                n = ChunkSectionPos.offset(n, Direction.DOWN);
            }
            if (this.hasSection(n)) {
                ((Data)this.storage).columnToTopSection.put(m, i + 1);
                if (bl) {
                    this.enqueueAddSection(n);
                }
            } else {
                ((Data)this.storage).columnToTopSection.remove(m);
            }
        }
        if (bl) {
            this.checkForUpdates();
        }
    }

    @Override
    protected void setColumnEnabled(long columnPos, boolean enabled) {
        this.updateAll();
        if (enabled && this.enabledColumns.add(columnPos)) {
            int i = ((Data)this.storage).columnToTopSection.get(columnPos);
            if (i != ((Data)this.storage).minSectionY) {
                long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(columnPos), i - 1, ChunkSectionPos.getZ(columnPos));
                this.enqueueAddSection(m);
                this.checkForUpdates();
            }
        } else if (!enabled) {
            this.enabledColumns.remove(columnPos);
        }
    }

    @Override
    protected boolean hasLightUpdates() {
        return super.hasLightUpdates() || this.hasUpdates;
    }

    @Override
    protected ChunkNibbleArray createSection(long sectionPos) {
        ChunkNibbleArray lv2;
        ChunkNibbleArray lv = (ChunkNibbleArray)this.queuedSections.get(sectionPos);
        if (lv != null) {
            return lv;
        }
        long m = ChunkSectionPos.offset(sectionPos, Direction.UP);
        int i = ((Data)this.storage).columnToTopSection.get(ChunkSectionPos.withZeroZ(sectionPos));
        if (i == ((Data)this.storage).minSectionY || ChunkSectionPos.getY(m) >= i) {
            return new ChunkNibbleArray();
        }
        while ((lv2 = this.getLightSection(m, true)) == null) {
            m = ChunkSectionPos.offset(m, Direction.UP);
        }
        return new ChunkNibbleArray(new ColumnChunkNibbleArray(lv2, 0).asByteArray());
    }

    @Override
    protected void updateLight(ChunkLightProvider<Data, ?> lightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
        LongIterator longIterator;
        super.updateLight(lightProvider, doSkylight, skipEdgeLightPropagation);
        if (!doSkylight) {
            return;
        }
        if (!this.sectionsToUpdate.isEmpty()) {
            longIterator = this.sectionsToUpdate.iterator();
            while (longIterator.hasNext()) {
                long l = (Long)longIterator.next();
                int i = this.getLevel(l);
                if (i == 2 || this.sectionsToRemove.contains(l) || !this.field_15820.add(l)) continue;
                if (i == 1) {
                    this.removeSection(lightProvider, l);
                    if (this.dirtySections.add(l)) {
                        ((Data)this.storage).replaceWithCopy(l);
                    }
                    Arrays.fill(this.getLightSection(l, true).asByteArray(), (byte)-1);
                    int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l));
                    int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l));
                    int m = ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l));
                    for (Direction lv : LIGHT_REDUCTION_DIRECTIONS) {
                        long n = ChunkSectionPos.offset(l, lv);
                        if (!this.sectionsToRemove.contains(n) && (this.field_15820.contains(n) || this.sectionsToUpdate.contains(n)) || !this.hasSection(n)) continue;
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
                                lightProvider.updateLevel(w, x, lightProvider.getPropagatedLevel(w, x, 0), true);
                            }
                        }
                    }
                    for (int y = 0; y < 16; ++y) {
                        for (int z = 0; z < 16; ++z) {
                            long aa = BlockPos.asLong(ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l)) + y, ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l)), ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l)) + z);
                            long ab = BlockPos.asLong(ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l)) + y, ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l)) - 1, ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l)) + z);
                            lightProvider.updateLevel(aa, ab, lightProvider.getPropagatedLevel(aa, ab, 0), true);
                        }
                    }
                    continue;
                }
                for (int ac = 0; ac < 16; ++ac) {
                    for (int ad = 0; ad < 16; ++ad) {
                        long ae = BlockPos.asLong(ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(l)) + ac, ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(l)) + 16 - 1, ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(l)) + ad);
                        lightProvider.updateLevel(Long.MAX_VALUE, ae, 0, true);
                    }
                }
            }
        }
        this.sectionsToUpdate.clear();
        if (!this.sectionsToRemove.isEmpty()) {
            longIterator = this.sectionsToRemove.iterator();
            while (longIterator.hasNext()) {
                long af = (Long)longIterator.next();
                if (!this.field_15820.remove(af) || !this.hasSection(af)) continue;
                for (int ag = 0; ag < 16; ++ag) {
                    for (int ah = 0; ah < 16; ++ah) {
                        long ai = BlockPos.asLong(ChunkSectionPos.getBlockCoord(ChunkSectionPos.getX(af)) + ag, ChunkSectionPos.getBlockCoord(ChunkSectionPos.getY(af)) + 16 - 1, ChunkSectionPos.getBlockCoord(ChunkSectionPos.getZ(af)) + ah);
                        lightProvider.updateLevel(Long.MAX_VALUE, ai, 15, false);
                    }
                }
            }
        }
        this.sectionsToRemove.clear();
        this.hasUpdates = false;
    }

    protected boolean isAboveMinHeight(int sectionY) {
        return sectionY >= ((Data)this.storage).minSectionY;
    }

    protected boolean isTopmostBlock(long blockPos) {
        int i = BlockPos.unpackLongY(blockPos);
        if ((i & 0xF) != 15) {
            return false;
        }
        long m = ChunkSectionPos.fromBlockPos(blockPos);
        long n = ChunkSectionPos.withZeroZ(m);
        if (!this.enabledColumns.contains(n)) {
            return false;
        }
        int j = ((Data)this.storage).columnToTopSection.get(n);
        return ChunkSectionPos.getBlockCoord(j) == i + 16;
    }

    protected boolean isAtOrAboveTopmostSection(long sectionPos) {
        long m = ChunkSectionPos.withZeroZ(sectionPos);
        int i = ((Data)this.storage).columnToTopSection.get(m);
        return i == ((Data)this.storage).minSectionY || ChunkSectionPos.getY(sectionPos) >= i;
    }

    protected boolean isSectionEnabled(long sectionPos) {
        long m = ChunkSectionPos.withZeroZ(sectionPos);
        return this.enabledColumns.contains(m);
    }

    public static final class Data
    extends ChunkToNibbleArrayMap<Data> {
        private int minSectionY;
        private final Long2IntOpenHashMap columnToTopSection;

        public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> arrays, Long2IntOpenHashMap columnToTopSection, int minSectionY) {
            super(arrays);
            this.columnToTopSection = columnToTopSection;
            columnToTopSection.defaultReturnValue(minSectionY);
            this.minSectionY = minSectionY;
        }

        @Override
        public Data copy() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arrays.clone(), this.columnToTopSection.clone(), this.minSectionY);
        }

        @Override
        public /* synthetic */ ChunkToNibbleArrayMap copy() {
            return this.copy();
        }
    }
}

