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
    private ChunkSectionPos(int x, int y, int z) {
        super(x, y, z);
    }

    public static ChunkSectionPos from(int x, int y, int z) {
        return new ChunkSectionPos(x, y, z);
    }

    public static ChunkSectionPos from(BlockPos pos) {
        return new ChunkSectionPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getY()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    public static ChunkSectionPos from(ChunkPos chunkPos, int y) {
        return new ChunkSectionPos(chunkPos.x, y, chunkPos.z);
    }

    public static ChunkSectionPos from(Entity entity) {
        return new ChunkSectionPos(ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getX())), ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getY())), ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getZ())));
    }

    public static ChunkSectionPos from(long packed) {
        return new ChunkSectionPos(ChunkSectionPos.getX(packed), ChunkSectionPos.getY(packed), ChunkSectionPos.getZ(packed));
    }

    public static long offset(long packed, Direction direction) {
        return ChunkSectionPos.offset(packed, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long offset(long packed, int x, int y, int z) {
        return ChunkSectionPos.asLong(ChunkSectionPos.getX(packed) + x, ChunkSectionPos.getY(packed) + y, ChunkSectionPos.getZ(packed) + z);
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }

    public static int getLocalCoord(int coord) {
        return coord & 0xF;
    }

    public static short getPackedLocalPos(BlockPos pos) {
        int i = ChunkSectionPos.getLocalCoord(pos.getX());
        int j = ChunkSectionPos.getLocalCoord(pos.getY());
        int k = ChunkSectionPos.getLocalCoord(pos.getZ());
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

    public static int getBlockCoord(int sectionCoord) {
        return sectionCoord << 4;
    }

    public static int getX(long packed) {
        return (int)(packed << 0 >> 42);
    }

    public static int getY(long packed) {
        return (int)(packed << 44 >> 44);
    }

    public static int getZ(long packed) {
        return (int)(packed << 22 >> 42);
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

    public static long fromBlockPos(long blockPos) {
        return ChunkSectionPos.asLong(ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getSectionCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(blockPos)));
    }

    public static long withZeroZ(long pos) {
        return pos & 0xFFFFFFFFFFF00000L;
    }

    public BlockPos getMinPos() {
        return new BlockPos(ChunkSectionPos.getBlockCoord(this.getSectionX()), ChunkSectionPos.getBlockCoord(this.getSectionY()), ChunkSectionPos.getBlockCoord(this.getSectionZ()));
    }

    public BlockPos getCenterPos() {
        int i = 8;
        return this.getMinPos().add(8, 8, 8);
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(this.getSectionX(), this.getSectionZ());
    }

    public static long asLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long)x & 0x3FFFFFL) << 42;
        l |= ((long)y & 0xFFFFFL) << 0;
        return l |= ((long)z & 0x3FFFFFL) << 20;
    }

    public long asLong() {
        return ChunkSectionPos.asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
    }

    public Stream<BlockPos> streamBlocks() {
        return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

    public static Stream<ChunkSectionPos> stream(ChunkSectionPos center, int radius) {
        int j = center.getSectionX();
        int k = center.getSectionY();
        int l = center.getSectionZ();
        return ChunkSectionPos.stream(j - radius, k - radius, l - radius, j + radius, k + radius, l + radius);
    }

    public static Stream<ChunkSectionPos> stream(ChunkPos center, int radius) {
        int j = center.x;
        int k = center.z;
        return ChunkSectionPos.stream(j - radius, 0, k - radius, j + radius, 15, k + radius);
    }

    public static Stream<ChunkSectionPos> stream(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkSectionPos>((long)((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)), 64){
            final CuboidBlockIterator iterator;
            {
                super(l, i);
                this.iterator = new CuboidBlockIterator(minX, minY, minZ, maxX, maxY, maxZ);
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

