/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.shape;

import java.util.BitSet;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.PairList;
import net.minecraft.util.shape.VoxelSet;

public final class BitSetVoxelSet
extends VoxelSet {
    private final BitSet storage;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;

    public BitSetVoxelSet(int i, int j, int k) {
        this(i, j, k, i, j, k, 0, 0, 0);
    }

    public BitSetVoxelSet(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        super(i, j, k);
        this.storage = new BitSet(i * j * k);
        this.xMin = l;
        this.yMin = m;
        this.zMin = n;
        this.xMax = o;
        this.yMax = p;
        this.zMax = q;
    }

    public BitSetVoxelSet(VoxelSet arg) {
        super(arg.xSize, arg.ySize, arg.zSize);
        if (arg instanceof BitSetVoxelSet) {
            this.storage = (BitSet)((BitSetVoxelSet)arg).storage.clone();
        } else {
            this.storage = new BitSet(this.xSize * this.ySize * this.zSize);
            for (int i = 0; i < this.xSize; ++i) {
                for (int j = 0; j < this.ySize; ++j) {
                    for (int k = 0; k < this.zSize; ++k) {
                        if (!arg.contains(i, j, k)) continue;
                        this.storage.set(this.getIndex(i, j, k));
                    }
                }
            }
        }
        this.xMin = arg.getMin(Direction.Axis.X);
        this.yMin = arg.getMin(Direction.Axis.Y);
        this.zMin = arg.getMin(Direction.Axis.Z);
        this.xMax = arg.getMax(Direction.Axis.X);
        this.yMax = arg.getMax(Direction.Axis.Y);
        this.zMax = arg.getMax(Direction.Axis.Z);
    }

    protected int getIndex(int i, int j, int k) {
        return (i * this.ySize + j) * this.zSize + k;
    }

    @Override
    public boolean contains(int i, int j, int k) {
        return this.storage.get(this.getIndex(i, j, k));
    }

    @Override
    public void set(int i, int j, int k, boolean bl, boolean bl2) {
        this.storage.set(this.getIndex(i, j, k), bl2);
        if (bl && bl2) {
            this.xMin = Math.min(this.xMin, i);
            this.yMin = Math.min(this.yMin, j);
            this.zMin = Math.min(this.zMin, k);
            this.xMax = Math.max(this.xMax, i + 1);
            this.yMax = Math.max(this.yMax, j + 1);
            this.zMax = Math.max(this.zMax, k + 1);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    @Override
    public int getMin(Direction.Axis arg) {
        return arg.choose(this.xMin, this.yMin, this.zMin);
    }

    @Override
    public int getMax(Direction.Axis arg) {
        return arg.choose(this.xMax, this.yMax, this.zMax);
    }

    @Override
    protected boolean isColumnFull(int i, int j, int k, int l) {
        if (k < 0 || l < 0 || i < 0) {
            return false;
        }
        if (k >= this.xSize || l >= this.ySize || j > this.zSize) {
            return false;
        }
        return this.storage.nextClearBit(this.getIndex(k, l, i)) >= this.getIndex(k, l, j);
    }

    @Override
    protected void setColumn(int i, int j, int k, int l, boolean bl) {
        this.storage.set(this.getIndex(k, l, i), this.getIndex(k, l, j), bl);
    }

    static BitSetVoxelSet combine(VoxelSet arg, VoxelSet arg2, PairList arg3, PairList arg4, PairList arg5, BooleanBiFunction arg6) {
        BitSetVoxelSet lv = new BitSetVoxelSet(arg3.getPairs().size() - 1, arg4.getPairs().size() - 1, arg5.getPairs().size() - 1);
        int[] is = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        arg3.forEachPair((i, j, k) -> {
            boolean[] bls = new boolean[]{false};
            boolean bl = arg4.forEachPair((l, m, n) -> {
                boolean[] bls2 = new boolean[]{false};
                boolean bl = arg5.forEachPair((o, p, q) -> {
                    boolean bl = arg6.apply(arg.inBoundsAndContains(i, l, o), arg2.inBoundsAndContains(j, m, p));
                    if (bl) {
                        arg4.storage.set(lv.getIndex(k, n, q));
                        is[2] = Math.min(is[2], q);
                        is[5] = Math.max(is[5], q);
                        bls[0] = true;
                    }
                    return true;
                });
                if (bls2[0]) {
                    is[1] = Math.min(is[1], n);
                    is[4] = Math.max(is[4], n);
                    bls[0] = true;
                }
                return bl;
            });
            if (bls[0]) {
                is[0] = Math.min(is[0], k);
                is[3] = Math.max(is[3], k);
            }
            return bl;
        });
        lv.xMin = is[0];
        lv.yMin = is[1];
        lv.zMin = is[2];
        lv.xMax = is[3] + 1;
        lv.yMax = is[4] + 1;
        lv.zMax = is[5] + 1;
        return lv;
    }
}

