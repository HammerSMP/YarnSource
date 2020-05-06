/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

public class CuboidBlockIterator {
    private int startX;
    private int startY;
    private int startZ;
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private int totalSize;
    private int blocksIterated;
    private int x;
    private int y;
    private int z;

    public CuboidBlockIterator(int i, int j, int k, int l, int m, int n) {
        this.startX = i;
        this.startY = j;
        this.startZ = k;
        this.sizeX = l - i + 1;
        this.sizeY = m - j + 1;
        this.sizeZ = n - k + 1;
        this.totalSize = this.sizeX * this.sizeY * this.sizeZ;
    }

    public boolean step() {
        if (this.blocksIterated == this.totalSize) {
            return false;
        }
        this.x = this.blocksIterated % this.sizeX;
        int i = this.blocksIterated / this.sizeX;
        this.y = i % this.sizeY;
        this.z = i / this.sizeY;
        ++this.blocksIterated;
        return true;
    }

    public int getX() {
        return this.startX + this.x;
    }

    public int getY() {
        return this.startY + this.y;
    }

    public int getZ() {
        return this.startZ + this.z;
    }

    public int getEdgeCoordinatesCount() {
        int i = 0;
        if (this.x == 0 || this.x == this.sizeX - 1) {
            ++i;
        }
        if (this.y == 0 || this.y == this.sizeY - 1) {
            ++i;
        }
        if (this.z == 0 || this.z == this.sizeZ - 1) {
            ++i;
        }
        return i;
    }
}

