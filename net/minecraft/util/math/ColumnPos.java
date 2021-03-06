/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.BlockPos;

public class ColumnPos {
    public final int x;
    public final int z;

    public ColumnPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ColumnPos(BlockPos pos) {
        this.x = pos.getX();
        this.z = pos.getZ();
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public int hashCode() {
        int i = 1664525 * this.x + 1013904223;
        int j = 1664525 * (this.z ^ 0xDEADBEEF) + 1013904223;
        return i ^ j;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ColumnPos) {
            ColumnPos lv = (ColumnPos)object;
            return this.x == lv.x && this.z == lv.z;
        }
        return false;
    }
}

