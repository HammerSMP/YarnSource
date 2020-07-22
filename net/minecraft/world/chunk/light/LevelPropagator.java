/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongList
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.function.LongPredicate;
import net.minecraft.util.math.MathHelper;

public abstract class LevelPropagator {
    private final int levelCount;
    private final LongLinkedOpenHashSet[] pendingIdUpdatesByLevel;
    private final Long2ByteMap pendingUpdates;
    private int minPendingLevel;
    private volatile boolean hasPendingUpdates;

    protected LevelPropagator(int levelCount, final int expectedLevelSize, final int expectedTotalSize) {
        if (levelCount >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = levelCount;
        this.pendingIdUpdatesByLevel = new LongLinkedOpenHashSet[levelCount];
        for (int l = 0; l < levelCount; ++l) {
            this.pendingIdUpdatesByLevel[l] = new LongLinkedOpenHashSet(expectedLevelSize, 0.5f){

                protected void rehash(int newN) {
                    if (newN > expectedLevelSize) {
                        super.rehash(newN);
                    }
                }
            };
        }
        this.pendingUpdates = new Long2ByteOpenHashMap(expectedTotalSize, 0.5f){

            protected void rehash(int newN) {
                if (newN > expectedTotalSize) {
                    super.rehash(newN);
                }
            }
        };
        this.pendingUpdates.defaultReturnValue((byte)-1);
        this.minPendingLevel = levelCount;
    }

    private int minLevel(int a, int b) {
        int k = a;
        if (k > b) {
            k = b;
        }
        if (k > this.levelCount - 1) {
            k = this.levelCount - 1;
        }
        return k;
    }

    private void increaseMinPendingLevel(int maxLevel) {
        int j = this.minPendingLevel;
        this.minPendingLevel = maxLevel;
        for (int k = j + 1; k < maxLevel; ++k) {
            if (this.pendingIdUpdatesByLevel[k].isEmpty()) continue;
            this.minPendingLevel = k;
            break;
        }
    }

    protected void removePendingUpdate(long id) {
        int i = this.pendingUpdates.get(id) & 0xFF;
        if (i == 255) {
            return;
        }
        int j = this.getLevel(id);
        int k = this.minLevel(j, i);
        this.removePendingUpdate(id, k, this.levelCount, true);
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
    }

    public void removePendingUpdateIf(LongPredicate predicate) {
        LongArrayList longList = new LongArrayList();
        this.pendingUpdates.keySet().forEach(arg_0 -> LevelPropagator.method_24207(predicate, (LongList)longList, arg_0));
        longList.forEach(this::removePendingUpdate);
    }

    private void removePendingUpdate(long id, int level, int levelCount, boolean removeFully) {
        if (removeFully) {
            this.pendingUpdates.remove(id);
        }
        this.pendingIdUpdatesByLevel[level].remove(id);
        if (this.pendingIdUpdatesByLevel[level].isEmpty() && this.minPendingLevel == level) {
            this.increaseMinPendingLevel(levelCount);
        }
    }

    private void addPendingUpdate(long id, int level, int targetLevel) {
        this.pendingUpdates.put(id, (byte)level);
        this.pendingIdUpdatesByLevel[targetLevel].add(id);
        if (this.minPendingLevel > targetLevel) {
            this.minPendingLevel = targetLevel;
        }
    }

    protected void resetLevel(long id) {
        this.updateLevel(id, id, this.levelCount - 1, false);
    }

    protected void updateLevel(long sourceId, long id, int level, boolean decrease) {
        this.updateLevel(sourceId, id, level, this.getLevel(id), this.pendingUpdates.get(id) & 0xFF, decrease);
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
    }

    private void updateLevel(long sourceId, long id, int level, int currentLevel, int pendingLevel, boolean decrease) {
        int o;
        boolean bl3;
        if (this.isMarker(id)) {
            return;
        }
        level = MathHelper.clamp(level, 0, this.levelCount - 1);
        currentLevel = MathHelper.clamp(currentLevel, 0, this.levelCount - 1);
        if (pendingLevel == 255) {
            boolean bl2 = true;
            pendingLevel = currentLevel;
        } else {
            bl3 = false;
        }
        if (decrease) {
            int n = Math.min(pendingLevel, level);
        } else {
            o = MathHelper.clamp(this.recalculateLevel(id, sourceId, level), 0, this.levelCount - 1);
        }
        int p = this.minLevel(currentLevel, pendingLevel);
        if (currentLevel != o) {
            int q = this.minLevel(currentLevel, o);
            if (p != q && !bl3) {
                this.removePendingUpdate(id, p, q, false);
            }
            this.addPendingUpdate(id, o, q);
        } else if (!bl3) {
            this.removePendingUpdate(id, p, this.levelCount, true);
        }
    }

    protected final void propagateLevel(long sourceId, long targetId, int level, boolean decrease) {
        int j = this.pendingUpdates.get(targetId) & 0xFF;
        int k = MathHelper.clamp(this.getPropagatedLevel(sourceId, targetId, level), 0, this.levelCount - 1);
        if (decrease) {
            this.updateLevel(sourceId, targetId, k, this.getLevel(targetId), j, true);
        } else {
            boolean bl3;
            int o;
            if (j == 255) {
                boolean bl2 = true;
                int n = MathHelper.clamp(this.getLevel(targetId), 0, this.levelCount - 1);
            } else {
                o = j;
                bl3 = false;
            }
            if (k == o) {
                this.updateLevel(sourceId, targetId, this.levelCount - 1, bl3 ? o : this.getLevel(targetId), j, false);
            }
        }
    }

    protected final boolean hasPendingUpdates() {
        return this.hasPendingUpdates;
    }

    protected final int applyPendingUpdates(int maxSteps) {
        if (this.minPendingLevel >= this.levelCount) {
            return maxSteps;
        }
        while (this.minPendingLevel < this.levelCount && maxSteps > 0) {
            int k;
            --maxSteps;
            LongLinkedOpenHashSet longLinkedOpenHashSet = this.pendingIdUpdatesByLevel[this.minPendingLevel];
            long l = longLinkedOpenHashSet.removeFirstLong();
            int j = MathHelper.clamp(this.getLevel(l), 0, this.levelCount - 1);
            if (longLinkedOpenHashSet.isEmpty()) {
                this.increaseMinPendingLevel(this.levelCount);
            }
            if ((k = this.pendingUpdates.remove(l) & 0xFF) < j) {
                this.setLevel(l, k);
                this.propagateLevel(l, k, true);
                continue;
            }
            if (k <= j) continue;
            this.addPendingUpdate(l, k, this.minLevel(this.levelCount - 1, k));
            this.setLevel(l, this.levelCount - 1);
            this.propagateLevel(l, j, false);
        }
        this.hasPendingUpdates = this.minPendingLevel < this.levelCount;
        return maxSteps;
    }

    public int getPendingUpdateCount() {
        return this.pendingUpdates.size();
    }

    protected abstract boolean isMarker(long var1);

    protected abstract int recalculateLevel(long var1, long var3, int var5);

    protected abstract void propagateLevel(long var1, int var3, boolean var4);

    protected abstract int getLevel(long var1);

    protected abstract void setLevel(long var1, int var3);

    protected abstract int getPropagatedLevel(long var1, long var3, int var5);

    private static /* synthetic */ void method_24207(LongPredicate longPredicate, LongList longList, long l) {
        if (longPredicate.test(l)) {
            longList.add(l);
        }
    }
}

