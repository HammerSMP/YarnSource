/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.light.LevelPropagator;

public abstract class SectionDistanceLevelPropagator
extends LevelPropagator {
    protected SectionDistanceLevelPropagator(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected boolean isMarker(long l) {
        return l == Long.MAX_VALUE;
    }

    @Override
    protected void propagateLevel(long l, int i, boolean bl) {
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int m = -1; m <= 1; ++m) {
                    long n = ChunkSectionPos.offset(l, j, k, m);
                    if (n == l) continue;
                    this.propagateLevel(l, n, i, bl);
                }
            }
        }
    }

    @Override
    protected int recalculateLevel(long l, long m, int i) {
        int j = i;
        for (int k = -1; k <= 1; ++k) {
            for (int n = -1; n <= 1; ++n) {
                for (int o = -1; o <= 1; ++o) {
                    long p = ChunkSectionPos.offset(l, k, n, o);
                    if (p == l) {
                        p = Long.MAX_VALUE;
                    }
                    if (p == m) continue;
                    int q = this.getPropagatedLevel(p, l, this.getLevel(p));
                    if (j > q) {
                        j = q;
                    }
                    if (j != 0) continue;
                    return j;
                }
            }
        }
        return j;
    }

    @Override
    protected int getPropagatedLevel(long l, long m, int i) {
        if (l == Long.MAX_VALUE) {
            return this.getInitialLevel(m);
        }
        return i + 1;
    }

    protected abstract int getInitialLevel(long var1);

    public void update(long l, int i, boolean bl) {
        this.updateLevel(Long.MAX_VALUE, l, i, bl);
    }
}

