/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class BlockBox {
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public BlockBox() {
    }

    public BlockBox(int[] is) {
        if (is.length == 6) {
            this.minX = is[0];
            this.minY = is[1];
            this.minZ = is[2];
            this.maxX = is[3];
            this.maxY = is[4];
            this.maxZ = is[5];
        }
    }

    public static BlockBox empty() {
        return new BlockBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static BlockBox infinite() {
        return new BlockBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static BlockBox rotated(int i, int j, int k, int l, int m, int n, int o, int p, int q, Direction arg) {
        switch (arg) {
            default: {
                return new BlockBox(i + l, j + m, k + n, i + o - 1 + l, j + p - 1 + m, k + q - 1 + n);
            }
            case NORTH: {
                return new BlockBox(i + l, j + m, k - q + 1 + n, i + o - 1 + l, j + p - 1 + m, k + n);
            }
            case SOUTH: {
                return new BlockBox(i + l, j + m, k + n, i + o - 1 + l, j + p - 1 + m, k + q - 1 + n);
            }
            case WEST: {
                return new BlockBox(i - q + 1 + n, j + m, k + l, i + n, j + p - 1 + m, k + o - 1 + l);
            }
            case EAST: 
        }
        return new BlockBox(i + n, j + m, k + l, i + q - 1 + n, j + p - 1 + m, k + o - 1 + l);
    }

    public static BlockBox create(int i, int j, int k, int l, int m, int n) {
        return new BlockBox(Math.min(i, l), Math.min(j, m), Math.min(k, n), Math.max(i, l), Math.max(j, m), Math.max(k, n));
    }

    public BlockBox(BlockBox arg) {
        this.minX = arg.minX;
        this.minY = arg.minY;
        this.minZ = arg.minZ;
        this.maxX = arg.maxX;
        this.maxY = arg.maxY;
        this.maxZ = arg.maxZ;
    }

    public BlockBox(int i, int j, int k, int l, int m, int n) {
        this.minX = i;
        this.minY = j;
        this.minZ = k;
        this.maxX = l;
        this.maxY = m;
        this.maxZ = n;
    }

    public BlockBox(Vec3i arg, Vec3i arg2) {
        this.minX = Math.min(arg.getX(), arg2.getX());
        this.minY = Math.min(arg.getY(), arg2.getY());
        this.minZ = Math.min(arg.getZ(), arg2.getZ());
        this.maxX = Math.max(arg.getX(), arg2.getX());
        this.maxY = Math.max(arg.getY(), arg2.getY());
        this.maxZ = Math.max(arg.getZ(), arg2.getZ());
    }

    public BlockBox(int i, int j, int k, int l) {
        this.minX = i;
        this.minZ = j;
        this.maxX = k;
        this.maxZ = l;
        this.minY = 1;
        this.maxY = 512;
    }

    public boolean intersects(BlockBox arg) {
        return this.maxX >= arg.minX && this.minX <= arg.maxX && this.maxZ >= arg.minZ && this.minZ <= arg.maxZ && this.maxY >= arg.minY && this.minY <= arg.maxY;
    }

    public boolean intersectsXZ(int i, int j, int k, int l) {
        return this.maxX >= i && this.minX <= k && this.maxZ >= j && this.minZ <= l;
    }

    public void encompass(BlockBox arg) {
        this.minX = Math.min(this.minX, arg.minX);
        this.minY = Math.min(this.minY, arg.minY);
        this.minZ = Math.min(this.minZ, arg.minZ);
        this.maxX = Math.max(this.maxX, arg.maxX);
        this.maxY = Math.max(this.maxY, arg.maxY);
        this.maxZ = Math.max(this.maxZ, arg.maxZ);
    }

    public void offset(int i, int j, int k) {
        this.minX += i;
        this.minY += j;
        this.minZ += k;
        this.maxX += i;
        this.maxY += j;
        this.maxZ += k;
    }

    public BlockBox translated(int i, int j, int k) {
        return new BlockBox(this.minX + i, this.minY + j, this.minZ + k, this.maxX + i, this.maxY + j, this.maxZ + k);
    }

    public boolean contains(Vec3i arg) {
        return arg.getX() >= this.minX && arg.getX() <= this.maxX && arg.getZ() >= this.minZ && arg.getZ() <= this.maxZ && arg.getY() >= this.minY && arg.getY() <= this.maxY;
    }

    public Vec3i getDimensions() {
        return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
    }

    public int getBlockCountX() {
        return this.maxX - this.minX + 1;
    }

    public int getBlockCountY() {
        return this.maxY - this.minY + 1;
    }

    public int getBlockCountZ() {
        return this.maxZ - this.minZ + 1;
    }

    public Vec3i getCenter() {
        return new BlockPos(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("x0", this.minX).add("y0", this.minY).add("z0", this.minZ).add("x1", this.maxX).add("y1", this.maxY).add("z1", this.maxZ).toString();
    }

    public IntArrayTag toNbt() {
        return new IntArrayTag(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
    }
}

