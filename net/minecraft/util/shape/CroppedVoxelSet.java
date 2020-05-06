/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.shape;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;

public final class CroppedVoxelSet
extends VoxelSet {
    private final VoxelSet parent;
    private final int xMin;
    private final int yMin;
    private final int zMin;
    private final int xMax;
    private final int yMax;
    private final int zMax;

    protected CroppedVoxelSet(VoxelSet arg, int i, int j, int k, int l, int m, int n) {
        super(l - i, m - j, n - k);
        this.parent = arg;
        this.xMin = i;
        this.yMin = j;
        this.zMin = k;
        this.xMax = l;
        this.yMax = m;
        this.zMax = n;
    }

    @Override
    public boolean contains(int i, int j, int k) {
        return this.parent.contains(this.xMin + i, this.yMin + j, this.zMin + k);
    }

    @Override
    public void set(int i, int j, int k, boolean bl, boolean bl2) {
        this.parent.set(this.xMin + i, this.yMin + j, this.zMin + k, bl, bl2);
    }

    @Override
    public int getMin(Direction.Axis arg) {
        return Math.max(0, this.parent.getMin(arg) - arg.choose(this.xMin, this.yMin, this.zMin));
    }

    @Override
    public int getMax(Direction.Axis arg) {
        return Math.min(arg.choose(this.xMax, this.yMax, this.zMax), this.parent.getMax(arg) - arg.choose(this.xMin, this.yMin, this.zMin));
    }
}

