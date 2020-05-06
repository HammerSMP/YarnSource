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
    protected boolean isMarker(long l) {
        return l == ChunkPos.MARKER;
    }

    @Override
    protected void propagateLevel(long l, int i, boolean bl) {
        ChunkPos lv = new ChunkPos(l);
        int j = lv.x;
        int k = lv.z;
        for (int m = -1; m <= 1; ++m) {
            for (int n = -1; n <= 1; ++n) {
                long o = ChunkPos.toLong(j + m, k + n);
                if (o == l) continue;
                this.propagateLevel(l, o, i, bl);
            }
        }
    }

    @Override
    protected int recalculateLevel(long l, long m, int i) {
        int j = i;
        ChunkPos lv = new ChunkPos(l);
        int k = lv.x;
        int n = lv.z;
        for (int o = -1; o <= 1; ++o) {
            for (int p = -1; p <= 1; ++p) {
                long q = ChunkPos.toLong(k + o, n + p);
                if (q == l) {
                    q = ChunkPos.MARKER;
                }
                if (q == m) continue;
                int r = this.getPropagatedLevel(q, l, this.getLevel(q));
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
    protected int getPropagatedLevel(long l, long m, int i) {
        if (l == ChunkPos.MARKER) {
            return this.getInitialLevel(m);
        }
        return i + 1;
    }

    protected abstract int getInitialLevel(long var1);

    public void updateLevel(long l, int i, boolean bl) {
        this.updateLevel(ChunkPos.MARKER, l, i, bl);
    }
}

