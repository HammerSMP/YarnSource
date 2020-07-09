/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class ChunkSectionPos
extends Vec3i {
    private ChunkSectionPos(int i, int j, int k) {
        super(i, j, k);
    }

    public static ChunkSectionPos from(int i, int j, int k) {
        return new ChunkSectionPos(i, j, k);
    }

    public static ChunkSectionPos from(BlockPos arg) {
        return new ChunkSectionPos(ChunkSectionPos.getSectionCoord(arg.getX()), ChunkSectionPos.getSectionCoord(arg.getY()), ChunkSectionPos.getSectionCoord(arg.getZ()));
    }

    public static ChunkSectionPos from(ChunkPos arg, int i) {
        return new ChunkSectionPos(arg.x, i, arg.z);
    }

    public static ChunkSectionPos from(Entity arg) {
        return new ChunkSectionPos(ChunkSectionPos.getSectionCoord(MathHelper.floor(arg.getX())), ChunkSectionPos.getSectionCoord(MathHelper.floor(arg.getY())), ChunkSectionPos.getSectionCoord(MathHelper.floor(arg.getZ())));
    }

    public static ChunkSectionPos from(long l) {
        return new ChunkSectionPos(ChunkSectionPos.getX(l), ChunkSectionPos.getY(l), ChunkSectionPos.getZ(l));
    }

    public static long offset(long l, Direction arg) {
        return ChunkSectionPos.offset(l, arg.getOffsetX(), arg.getOffsetY(), arg.getOffsetZ());
    }

    public static long offset(long l, int i, int j, int k) {
        return ChunkSectionPos.asLong(ChunkSectionPos.getX(l) + i, ChunkSectionPos.getY(l) + j, ChunkSectionPos.getZ(l) + k);
    }

    public static int getSectionCoord(int i) {
        return i >> 4;
    }

    public static int getLocalCoord(int i) {
        return i & 0xF;
    }

    public static short getPackedLocalPos(BlockPos arg) {
        int i = ChunkSectionPos.getLocalCoord(arg.getX());
        int j = ChunkSectionPos.getLocalCoord(arg.getY());
        int k = ChunkSectionPos.getLocalCoord(arg.getZ());
        return (short)(i << 8 | k << 4 | j << 0);
    }

    public static int method_30551(short s) {
        return s >>> 8 & 0xF;
    }

    public static int method_30552(short s) {
        return s >>> 0 & 0xF;
    }

    public static int method_30553(short s) {
        return s >>> 4 & 0xF;
    }

    public int method_30554(short s) {
        return this.getMinX() + ChunkSectionPos.method_30551(s);
    }

    public int method_30555(short s) {
        return this.getMinY() + ChunkSectionPos.method_30552(s);
    }

    public int method_30556(short s) {
        return this.getMinZ() + ChunkSectionPos.method_30553(s);
    }

    public BlockPos method_30557(short s) {
        return new BlockPos(this.method_30554(s), this.method_30555(s), this.method_30556(s));
    }

    public static int getWorldCoord(int i) {
        return i << 4;
    }

    public static int getX(long l) {
        return (int)(l << 0 >> 42);
    }

    public static int getY(long l) {
        return (int)(l << 44 >> 44);
    }

    public static int getZ(long l) {
        return (int)(l << 22 >> 42);
    }

    public int getSectionX() {
        return this.getX();
    }

    public int getSectionY() {
        return this.getY();
    }

    public int getSectionZ() {
        return this.getZ();
    }

    public int getMinX() {
        return this.getSectionX() << 4;
    }

    public int getMinY() {
        return this.getSectionY() << 4;
    }

    public int getMinZ() {
        return this.getSectionZ() << 4;
    }

    public int getMaxX() {
        return (this.getSectionX() << 4) + 15;
    }

    public int getMaxY() {
        return (this.getSectionY() << 4) + 15;
    }

    public int getMaxZ() {
        return (this.getSectionZ() << 4) + 15;
    }

    public static long fromGlobalPos(long l) {
        return ChunkSectionPos.asLong(ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getSectionCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(l)));
    }

    public static long withZeroZ(long l) {
        return l & 0xFFFFFFFFFFF00000L;
    }

    public BlockPos getMinPos() {
        return new BlockPos(ChunkSectionPos.getWorldCoord(this.getSectionX()), ChunkSectionPos.getWorldCoord(this.getSectionY()), ChunkSectionPos.getWorldCoord(this.getSectionZ()));
    }

    public BlockPos getCenterPos() {
        int i = 8;
        return this.getMinPos().add(8, 8, 8);
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(this.getSectionX(), this.getSectionZ());
    }

    public static long asLong(int i, int j, int k) {
        long l = 0L;
        l |= ((long)i & 0x3FFFFFL) << 42;
        l |= ((long)j & 0xFFFFFL) << 0;
        return l |= ((long)k & 0x3FFFFFL) << 20;
    }

    public long asLong() {
        return ChunkSectionPos.asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
    }

    public Stream<BlockPos> streamBlocks() {
        return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

    public static Stream<ChunkSectionPos> stream(ChunkSectionPos arg, int i) {
        int j = arg.getSectionX();
        int k = arg.getSectionY();
        int l = arg.getSectionZ();
        return ChunkSectionPos.stream(j - i, k - i, l - i, j + i, k + i, l + i);
    }

    public static Stream<ChunkSectionPos> stream(ChunkPos arg, int i) {
        int j = arg.x;
        int k = arg.z;
        return ChunkSectionPos.stream(j - i, 0, k - i, j + i, 15, k + i);
    }

    public static Stream<ChunkSectionPos> stream(final int i, final int j, final int k, final int l, final int m, final int n) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkSectionPos>((long)((l - i + 1) * (m - j + 1) * (n - k + 1)), 64){
            final CuboidBlockIterator iterator;
            {
                super(l2, i2);
                this.iterator = new CuboidBlockIterator(i, j, k, l, m, n);
            }

            @Override
            public boolean tryAdvance(Consumer<? super ChunkSectionPos> consumer) {
                if (this.iterator.step()) {
                    consumer.accept(new ChunkSectionPos(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }
}

