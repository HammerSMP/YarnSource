/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.level.storage;

public class AlphaChunkDataArray {
    public final byte[] data;
    private final int zOffset;
    private final int xOffset;

    public AlphaChunkDataArray(byte[] data, int yCoordinateBits) {
        this.data = data;
        this.zOffset = yCoordinateBits;
        this.xOffset = yCoordinateBits + 4;
    }

    public int get(int x, int y, int z) {
        int l = x << this.xOffset | z << this.zOffset | y;
        int m = l >> 1;
        int n = l & 1;
        if (n == 0) {
            return this.data[m] & 0xF;
        }
        return this.data[m] >> 4 & 0xF;
    }
}

