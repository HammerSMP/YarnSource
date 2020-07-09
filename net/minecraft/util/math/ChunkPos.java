/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class ChunkPos {
    public static final long MARKER = ChunkPos.toLong(1875016, 1875016);
    public final int x;
    public final int z;

    public ChunkPos(int i, int j) {
        this.x = i;
        this.z = j;
    }

    public ChunkPos(BlockPos arg) {
        this.x = arg.getX() >> 4;
        this.z = arg.getZ() >> 4;
    }

    public ChunkPos(long l) {
        this.x = (int)l;
        this.z = (int)(l >> 32);
    }

    public long toLong() {
        return ChunkPos.toLong(this.x, this.z);
    }

    public static long toLong(int i, int j) {
        return (long)i & 0xFFFFFFFFL | ((long)j & 0xFFFFFFFFL) << 32;
    }

    public static int getPackedX(long l) {
        return (int)(l & 0xFFFFFFFFL);
    }

    public static int getPackedZ(long l) {
        return (int)(l >>> 32 & 0xFFFFFFFFL);
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
        if (object instanceof ChunkPos) {
            ChunkPos lv = (ChunkPos)object;
            return this.x == lv.x && this.z == lv.z;
        }
        return false;
    }

    public int getStartX() {
        return this.x << 4;
    }

    public int getStartZ() {
        return this.z << 4;
    }

    public int getEndX() {
        return (this.x << 4) + 15;
    }

    public int getEndZ() {
        return (this.z << 4) + 15;
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int getRegionRelativeX() {
        return this.x & 0x1F;
    }

    public int getRegionRelativeZ() {
        return this.z & 0x1F;
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPos getCenterBlockPos() {
        return new BlockPos(this.getStartX(), 0, this.getStartZ());
    }

    public int method_24022(ChunkPos arg) {
        return Math.max(Math.abs(this.x - arg.x), Math.abs(this.z - arg.z));
    }

    public static Stream<ChunkPos> stream(ChunkPos arg, int i) {
        return ChunkPos.stream(new ChunkPos(arg.x - i, arg.z - i), new ChunkPos(arg.x + i, arg.z + i));
    }

    public static Stream<ChunkPos> stream(final ChunkPos arg, final ChunkPos arg2) {
        int i = Math.abs(arg.x - arg2.x) + 1;
        int j = Math.abs(arg.z - arg2.z) + 1;
        final int k = arg.x < arg2.x ? 1 : -1;
        final int l = arg.z < arg2.z ? 1 : -1;
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkPos>((long)(i * j), 64){
            @Nullable
            private ChunkPos position;

            @Override
            public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
                if (this.position == null) {
                    this.position = arg;
                } else {
                    int i = this.position.x;
                    int j = this.position.z;
                    if (i == arg2.x) {
                        if (j == arg2.z) {
                            return false;
                        }
                        this.position = new ChunkPos(arg.x, j + l);
                    } else {
                        this.position = new ChunkPos(i + k, j);
                    }
                }
                consumer.accept(this.position);
                return true;
            }
        }, false);
    }
}

