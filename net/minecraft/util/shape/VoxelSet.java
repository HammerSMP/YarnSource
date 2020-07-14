/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.shape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;

public abstract class VoxelSet {
    private static final Direction.Axis[] AXES = Direction.Axis.values();
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;

    protected VoxelSet(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public boolean inBoundsAndContains(AxisCycleDirection cycle, int x, int y, int z) {
        return this.inBoundsAndContains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
    }

    public boolean inBoundsAndContains(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) {
            return false;
        }
        if (x >= this.xSize || y >= this.ySize || z >= this.zSize) {
            return false;
        }
        return this.contains(x, y, z);
    }

    public boolean contains(AxisCycleDirection cycle, int x, int y, int z) {
        return this.contains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
    }

    public abstract boolean contains(int var1, int var2, int var3);

    public abstract void set(int var1, int var2, int var3, boolean var4, boolean var5);

    public boolean isEmpty() {
        for (Direction.Axis lv : AXES) {
            if (this.getMin(lv) < this.getMax(lv)) continue;
            return true;
        }
        return false;
    }

    public abstract int getMin(Direction.Axis var1);

    public abstract int getMax(Direction.Axis var1);

    @Environment(value=EnvType.CLIENT)
    public int getEndingAxisCoord(Direction.Axis arg, int from, int to) {
        if (from < 0 || to < 0) {
            return 0;
        }
        Direction.Axis lv = AxisCycleDirection.FORWARD.cycle(arg);
        Direction.Axis lv2 = AxisCycleDirection.BACKWARD.cycle(arg);
        if (from >= this.getSize(lv) || to >= this.getSize(lv2)) {
            return 0;
        }
        int k = this.getSize(arg);
        AxisCycleDirection lv3 = AxisCycleDirection.between(Direction.Axis.X, arg);
        for (int l = k - 1; l >= 0; --l) {
            if (!this.contains(lv3, l, from, to)) continue;
            return l + 1;
        }
        return 0;
    }

    public int getSize(Direction.Axis axis) {
        return axis.choose(this.xSize, this.ySize, this.zSize);
    }

    public int getXSize() {
        return this.getSize(Direction.Axis.X);
    }

    public int getYSize() {
        return this.getSize(Direction.Axis.Y);
    }

    public int getZSize() {
        return this.getSize(Direction.Axis.Z);
    }

    @Environment(value=EnvType.CLIENT)
    public void forEachEdge(PositionBiConsumer arg, boolean bl) {
        this.forEachEdge(arg, AxisCycleDirection.NONE, bl);
        this.forEachEdge(arg, AxisCycleDirection.FORWARD, bl);
        this.forEachEdge(arg, AxisCycleDirection.BACKWARD, bl);
    }

    @Environment(value=EnvType.CLIENT)
    private void forEachEdge(PositionBiConsumer arg, AxisCycleDirection direction, boolean bl) {
        AxisCycleDirection lv = direction.opposite();
        int i = this.getSize(lv.cycle(Direction.Axis.X));
        int j = this.getSize(lv.cycle(Direction.Axis.Y));
        int k = this.getSize(lv.cycle(Direction.Axis.Z));
        for (int l = 0; l <= i; ++l) {
            for (int m = 0; m <= j; ++m) {
                int n = -1;
                for (int o = 0; o <= k; ++o) {
                    int p = 0;
                    int q = 0;
                    for (int r = 0; r <= 1; ++r) {
                        for (int s = 0; s <= 1; ++s) {
                            if (!this.inBoundsAndContains(lv, l + r - 1, m + s - 1, o)) continue;
                            ++p;
                            q ^= r ^ s;
                        }
                    }
                    if (p == 1 || p == 3 || p == 2 && !(q & true)) {
                        if (bl) {
                            if (n != -1) continue;
                            n = o;
                            continue;
                        }
                        arg.consume(lv.choose(l, m, o, Direction.Axis.X), lv.choose(l, m, o, Direction.Axis.Y), lv.choose(l, m, o, Direction.Axis.Z), lv.choose(l, m, o + 1, Direction.Axis.X), lv.choose(l, m, o + 1, Direction.Axis.Y), lv.choose(l, m, o + 1, Direction.Axis.Z));
                        continue;
                    }
                    if (n == -1) continue;
                    arg.consume(lv.choose(l, m, n, Direction.Axis.X), lv.choose(l, m, n, Direction.Axis.Y), lv.choose(l, m, n, Direction.Axis.Z), lv.choose(l, m, o, Direction.Axis.X), lv.choose(l, m, o, Direction.Axis.Y), lv.choose(l, m, o, Direction.Axis.Z));
                    n = -1;
                }
            }
        }
    }

    protected boolean isColumnFull(int minZ, int maxZ, int x, int y) {
        for (int m = minZ; m < maxZ; ++m) {
            if (this.inBoundsAndContains(x, y, m)) continue;
            return false;
        }
        return true;
    }

    protected void setColumn(int minZ, int maxZ, int x, int y, boolean included) {
        for (int m = minZ; m < maxZ; ++m) {
            this.set(x, y, m, false, included);
        }
    }

    protected boolean isRectangleFull(int minX, int maxX, int minZ, int maxZ, int y) {
        for (int n = minX; n < maxX; ++n) {
            if (this.isColumnFull(minZ, maxZ, n, y)) continue;
            return false;
        }
        return true;
    }

    public void forEachBox(PositionBiConsumer consumer, boolean largest) {
        BitSetVoxelSet lv = new BitSetVoxelSet(this);
        for (int i = 0; i <= this.xSize; ++i) {
            for (int j = 0; j <= this.ySize; ++j) {
                int k = -1;
                for (int l = 0; l <= this.zSize; ++l) {
                    if (lv.inBoundsAndContains(i, j, l)) {
                        if (largest) {
                            if (k != -1) continue;
                            k = l;
                            continue;
                        }
                        consumer.consume(i, j, l, i + 1, j + 1, l + 1);
                        continue;
                    }
                    if (k == -1) continue;
                    int m = i;
                    int n = i;
                    int o = j;
                    int p = j;
                    ((VoxelSet)lv).setColumn(k, l, i, j, false);
                    while (((VoxelSet)lv).isColumnFull(k, l, m - 1, o)) {
                        ((VoxelSet)lv).setColumn(k, l, m - 1, o, false);
                        --m;
                    }
                    while (((VoxelSet)lv).isColumnFull(k, l, n + 1, o)) {
                        ((VoxelSet)lv).setColumn(k, l, n + 1, o, false);
                        ++n;
                    }
                    while (lv.isRectangleFull(m, n + 1, k, l, o - 1)) {
                        for (int q = m; q <= n; ++q) {
                            ((VoxelSet)lv).setColumn(k, l, q, o - 1, false);
                        }
                        --o;
                    }
                    while (lv.isRectangleFull(m, n + 1, k, l, p + 1)) {
                        for (int r = m; r <= n; ++r) {
                            ((VoxelSet)lv).setColumn(k, l, r, p + 1, false);
                        }
                        ++p;
                    }
                    consumer.consume(m, o, k, n + 1, p + 1, l);
                    k = -1;
                }
            }
        }
    }

    public void forEachDirection(PositionConsumer arg) {
        this.forEachDirection(arg, AxisCycleDirection.NONE);
        this.forEachDirection(arg, AxisCycleDirection.FORWARD);
        this.forEachDirection(arg, AxisCycleDirection.BACKWARD);
    }

    private void forEachDirection(PositionConsumer arg, AxisCycleDirection direction) {
        AxisCycleDirection lv = direction.opposite();
        Direction.Axis lv2 = lv.cycle(Direction.Axis.Z);
        int i = this.getSize(lv.cycle(Direction.Axis.X));
        int j = this.getSize(lv.cycle(Direction.Axis.Y));
        int k = this.getSize(lv2);
        Direction lv3 = Direction.from(lv2, Direction.AxisDirection.NEGATIVE);
        Direction lv4 = Direction.from(lv2, Direction.AxisDirection.POSITIVE);
        for (int l = 0; l < i; ++l) {
            for (int m = 0; m < j; ++m) {
                boolean bl = false;
                for (int n = 0; n <= k; ++n) {
                    boolean bl2;
                    boolean bl3 = bl2 = n != k && this.contains(lv, l, m, n);
                    if (!bl && bl2) {
                        arg.consume(lv3, lv.choose(l, m, n, Direction.Axis.X), lv.choose(l, m, n, Direction.Axis.Y), lv.choose(l, m, n, Direction.Axis.Z));
                    }
                    if (bl && !bl2) {
                        arg.consume(lv4, lv.choose(l, m, n - 1, Direction.Axis.X), lv.choose(l, m, n - 1, Direction.Axis.Y), lv.choose(l, m, n - 1, Direction.Axis.Z));
                    }
                    bl = bl2;
                }
            }
        }
    }

    public static interface PositionConsumer {
        public void consume(Direction var1, int var2, int var3, int var4);
    }

    public static interface PositionBiConsumer {
        public void consume(int var1, int var2, int var3, int var4, int var5, int var6);
    }
}

