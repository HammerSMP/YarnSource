/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.light.LevelPropagator;

public abstract class ChunkPosDistanceLevelPropagator
extends LevelPropagator {
    protected ChunkPosDistanceLevelPropagator(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected boolean isMarker(long id) {
        return id == ChunkPos.MARKER;
    }

    @Override
    protected void propagateLevel(long id, int level, boolean decrease) {
        ChunkPos lv = new ChunkPos(id);
        int j = lv.x;
        int k = lv.z;
        for (int m = -1; m <= 1; ++m) {
            for (int n = -1; n <= 1; ++n) {
                long o = ChunkPos.toLong(j + m, k + n);
                if (o == id) continue;
                this.propagateLevel(id, o, level, decrease);
            }
        }
    }

    @Override
    protected int recalculateLevel(long id, long excludedId, int maxLevel) {
        int j = maxLevel;
        ChunkPos lv = new ChunkPos(id);
        int k = lv.x;
        int n = lv.z;
        for (int o = -1; o <= 1; ++o) {
            for (int p = -1; p <= 1; ++p) {
                long q = ChunkPos.toLong(k + o, n + p);
                if (q == id) {
                    q = ChunkPos.MARKER;
                }
                if (q == excludedId) continue;
                int r = this.getPropagatedLevel(q, id, this.getLevel(q));
                if (j > r) {
                    j = r;
                }
                if (j != 0) continue;
                return j;
            }
        }
        return j;
    }

    @Override
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        if (sourceId == ChunkPos.MARKER) {
            return this.getInitialLevel(targetId);
        }
        return level + 1;
    }

    protected abstract int getInitialLevel(long var1);

    public void updateLevel(long chunkPos, int distance, boolean decrease) {
        this.updateLevel(ChunkPos.MARKER, chunkPos, distance, decrease);
    }
}

