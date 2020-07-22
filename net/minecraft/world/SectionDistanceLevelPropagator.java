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
    protected boolean isMarker(long id) {
        return id == Long.MAX_VALUE;
    }

    @Override
    protected void propagateLevel(long id, int level, boolean decrease) {
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int m = -1; m <= 1; ++m) {
                    long n = ChunkSectionPos.offset(id, j, k, m);
                    if (n == id) continue;
                    this.propagateLevel(id, n, level, decrease);
                }
            }
        }
    }

    @Override
    protected int recalculateLevel(long id, long excludedId, int maxLevel) {
        int j = maxLevel;
        for (int k = -1; k <= 1; ++k) {
            for (int n = -1; n <= 1; ++n) {
                for (int o = -1; o <= 1; ++o) {
                    long p = ChunkSectionPos.offset(id, k, n, o);
                    if (p == id) {
                        p = Long.MAX_VALUE;
                    }
                    if (p == excludedId) continue;
                    int q = this.getPropagatedLevel(p, id, this.getLevel(p));
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
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        if (sourceId == Long.MAX_VALUE) {
            return this.getInitialLevel(targetId);
        }
        return level + 1;
    }

    protected abstract int getInitialLevel(long var1);

    public void update(long id, int level, boolean decrease) {
        this.updateLevel(Long.MAX_VALUE, id, level, decrease);
    }
}

