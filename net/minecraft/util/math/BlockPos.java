/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.mojang.serialization.Codec
 *  javax.annotation.concurrent.Immutable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos
extends Vec3i {
    public static final Codec<BlockPos> field_25064 = Codec.INT_STREAM.comapFlatMap(intStream -> Util.toIntArray(intStream, 3).map(is -> new BlockPos(is[0], is[1], is[2])), arg -> IntStream.of(arg.getX(), arg.getY(), arg.getZ())).stable();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    private static final int SIZE_BITS_X;
    private static final int SIZE_BITS_Z;
    private static final int SIZE_BITS_Y;
    private static final long BITS_X;
    private static final long BITS_Y;
    private static final long BITS_Z;
    private static final int BIT_SHIFT_Z;
    private static final int BIT_SHIFT_X;

    public BlockPos(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPos(double d, double e, double f) {
        super(d, e, f);
    }

    public BlockPos(Vec3d pos) {
        this(pos.x, pos.y, pos.z);
    }

    public BlockPos(Position pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public static long offset(long value, Direction direction) {
        return BlockPos.add(value, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long add(long value, int x, int y, int z) {
        return BlockPos.asLong(BlockPos.unpackLongX(value) + x, BlockPos.unpackLongY(value) + y, BlockPos.unpackLongZ(value) + z);
    }

    public static int unpackLongX(long x) {
        return (int)(x << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
    }

    public static int unpackLongY(long y) {
        return (int)(y << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
    }

    public static int unpackLongZ(long z) {
        return (int)(z << 64 - BIT_SHIFT_Z - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
    }

    public static BlockPos fromLong(long value) {
        return new BlockPos(BlockPos.unpackLongX(value), BlockPos.unpackLongY(value), BlockPos.unpackLongZ(value));
    }

    public long asLong() {
        return BlockPos.asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long)x & BITS_X) << BIT_SHIFT_X;
        l |= ((long)y & BITS_Y) << 0;
        return l |= ((long)z & BITS_Z) << BIT_SHIFT_Z;
    }

    public static long removeChunkSectionLocalY(long y) {
        return y & 0xFFFFFFFFFFFFFFF0L;
    }

    public BlockPos add(double x, double y, double z) {
        if (x == 0.0 && y == 0.0 && z == 0.0) {
            return this;
        }
        return new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
    }

    public BlockPos add(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            return this;
        }
        return new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(Vec3i pos) {
        return this.add(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos subtract(Vec3i pos) {
        return this.add(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    public BlockPos up() {
        return this.offset(Direction.UP);
    }

    public BlockPos up(int distance) {
        return this.offset(Direction.UP, distance);
    }

    @Override
    public BlockPos down() {
        return this.offset(Direction.DOWN);
    }

    @Override
    public BlockPos down(int i) {
        return this.offset(Direction.DOWN, i);
    }

    public BlockPos north() {
        return this.offset(Direction.NORTH);
    }

    public BlockPos north(int distance) {
        return this.offset(Direction.NORTH, distance);
    }

    public BlockPos south() {
        return this.offset(Direction.SOUTH);
    }

    public BlockPos south(int distance) {
        return this.offset(Direction.SOUTH, distance);
    }

    public BlockPos west() {
        return this.offset(Direction.WEST);
    }

    public BlockPos west(int distance) {
        return this.offset(Direction.WEST, distance);
    }

    public BlockPos east() {
        return this.offset(Direction.EAST);
    }

    public BlockPos east(int distance) {
        return this.offset(Direction.EAST, distance);
    }

    public BlockPos offset(Direction direction) {
        return new BlockPos(this.getX() + direction.getOffsetX(), this.getY() + direction.getOffsetY(), this.getZ() + direction.getOffsetZ());
    }

    @Override
    public BlockPos offset(Direction arg, int i) {
        if (i == 0) {
            return this;
        }
        return new BlockPos(this.getX() + arg.getOffsetX() * i, this.getY() + arg.getOffsetY() * i, this.getZ() + arg.getOffsetZ() * i);
    }

    public BlockPos method_30513(Direction.Axis arg, int i) {
        if (i == 0) {
            return this;
        }
        int j = arg == Direction.Axis.X ? i : 0;
        int k = arg == Direction.Axis.Y ? i : 0;
        int l = arg == Direction.Axis.Z ? i : 0;
        return new BlockPos(this.getX() + j, this.getY() + k, this.getZ() + l);
    }

    public BlockPos rotate(BlockRotation rotation) {
        switch (rotation) {
            default: {
                return this;
            }
            case CLOCKWISE_90: {
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            }
            case CLOCKWISE_180: {
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            }
            case COUNTERCLOCKWISE_90: 
        }
        return new BlockPos(this.getZ(), this.getY(), -this.getX());
    }

    @Override
    public BlockPos crossProduct(Vec3i pos) {
        return new BlockPos(this.getY() * pos.getZ() - this.getZ() * pos.getY(), this.getZ() * pos.getX() - this.getX() * pos.getZ(), this.getX() * pos.getY() - this.getY() * pos.getX());
    }

    public BlockPos toImmutable() {
        return this;
    }

    public Mutable mutableCopy() {
        return new Mutable(this.getX(), this.getY(), this.getZ());
    }

    public static Iterable<BlockPos> method_27156(final Random random, final int i, final int j, final int k, final int l, int m, int n, int o) {
        final int p = m - j + 1;
        final int q = n - k + 1;
        final int r = o - l + 1;
        return () -> new AbstractIterator<BlockPos>(){
            final Mutable field_23945 = new Mutable();
            int field_23946 = i;

            protected BlockPos computeNext() {
                if (this.field_23946 <= 0) {
                    return (BlockPos)this.endOfData();
                }
                Mutable lv = this.field_23945.set(j + random.nextInt(p), k + random.nextInt(q), l + random.nextInt(r));
                --this.field_23946;
                return lv;
            }

            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    public static Iterable<BlockPos> iterateOutwards(BlockPos center, final int xRange, final int yRange, final int zRange) {
        final int l = xRange + yRange + zRange;
        final int m = center.getX();
        final int n = center.getY();
        final int o = center.getZ();
        return () -> new AbstractIterator<BlockPos>(){
            private final Mutable field_23378 = new Mutable();
            private int manhattanDistance;
            private int limitX;
            private int limitY;
            private int dx;
            private int dy;
            private boolean field_23379;

            protected BlockPos computeNext() {
                if (this.field_23379) {
                    this.field_23379 = false;
                    this.field_23378.setZ(o - (this.field_23378.getZ() - o));
                    return this.field_23378;
                }
                Mutable lv = null;
                while (lv == null) {
                    if (this.dy > this.limitY) {
                        ++this.dx;
                        if (this.dx > this.limitX) {
                            ++this.manhattanDistance;
                            if (this.manhattanDistance > l) {
                                return (BlockPos)this.endOfData();
                            }
                            this.limitX = Math.min(xRange, this.manhattanDistance);
                            this.dx = -this.limitX;
                        }
                        this.limitY = Math.min(yRange, this.manhattanDistance - Math.abs(this.dx));
                        this.dy = -this.limitY;
                    }
                    int i = this.dx;
                    int j = this.dy;
                    int k = this.manhattanDistance - Math.abs(i) - Math.abs(j);
                    if (k <= zRange) {
                        this.field_23379 = k != 0;
                        lv = this.field_23378.set(m + i, n + j, o + k);
                    }
                    ++this.dy;
                }
                return lv;
            }

            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
        return BlockPos.streamOutwards(pos, horizontalRange, verticalRange, horizontalRange).filter(condition).findFirst();
    }

    public static Stream<BlockPos> streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
        return StreamSupport.stream(BlockPos.iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
        return BlockPos.iterate(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()), Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
    }

    public static Stream<BlockPos> stream(BlockPos start, BlockPos end) {
        return StreamSupport.stream(BlockPos.iterate(start, end).spliterator(), false);
    }

    public static Stream<BlockPos> stream(BlockBox box) {
        return BlockPos.stream(Math.min(box.minX, box.maxX), Math.min(box.minY, box.maxY), Math.min(box.minZ, box.maxZ), Math.max(box.minX, box.maxX), Math.max(box.minY, box.maxY), Math.max(box.minZ, box.maxZ));
    }

    public static Stream<BlockPos> method_29715(Box arg) {
        return BlockPos.stream(MathHelper.floor(arg.minX), MathHelper.floor(arg.minY), MathHelper.floor(arg.minZ), MathHelper.floor(arg.maxX), MathHelper.floor(arg.maxY), MathHelper.floor(arg.maxZ));
    }

    public static Stream<BlockPos> stream(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        return StreamSupport.stream(BlockPos.iterate(startX, startY, startZ, endX, endY, endZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(final int startX, final int startY, final int startZ, int endX, int endY, int endZ) {
        final int o = endX - startX + 1;
        final int p = endY - startY + 1;
        int q = endZ - startZ + 1;
        final int r = o * p * q;
        return () -> new AbstractIterator<BlockPos>(){
            private final Mutable field_23380 = new Mutable();
            private int index;

            protected BlockPos computeNext() {
                if (this.index == r) {
                    return (BlockPos)this.endOfData();
                }
                int i = this.index % o;
                int j = this.index / o;
                int k = j % p;
                int l = j / p;
                ++this.index;
                return this.field_23380.set(startX + i, startY + k, startZ + l);
            }

            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    public static Iterable<Mutable> method_30512(final BlockPos arg, final int i, final Direction arg2, final Direction arg3) {
        Validate.validState((arg2.getAxis() != arg3.getAxis() ? 1 : 0) != 0, (String)"The two directions cannot be on the same axis", (Object[])new Object[0]);
        return () -> new AbstractIterator<Mutable>(){
            private final Direction[] field_25903;
            private final Mutable field_25904;
            private final int field_25905;
            private int field_25906;
            private int field_25907;
            private int field_25908;
            private int field_25909;
            private int field_25910;
            private int field_25911;
            {
                this.field_25903 = new Direction[]{arg2, arg3, arg2.getOpposite(), arg3.getOpposite()};
                this.field_25904 = arg.mutableCopy().move(arg3);
                this.field_25905 = 4 * i;
                this.field_25906 = -1;
                this.field_25909 = this.field_25904.getX();
                this.field_25910 = this.field_25904.getY();
                this.field_25911 = this.field_25904.getZ();
            }

            protected Mutable computeNext() {
                this.field_25904.set(this.field_25909, this.field_25910, this.field_25911).move(this.field_25903[(this.field_25906 + 4) % 4]);
                this.field_25909 = this.field_25904.getX();
                this.field_25910 = this.field_25904.getY();
                this.field_25911 = this.field_25904.getZ();
                if (this.field_25908 >= this.field_25907) {
                    if (this.field_25906 >= this.field_25905) {
                        return (Mutable)this.endOfData();
                    }
                    ++this.field_25906;
                    this.field_25908 = 0;
                    this.field_25907 = this.field_25906 / 2 + 1;
                }
                ++this.field_25908;
                return this.field_25904;
            }

            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    @Override
    public /* synthetic */ Vec3i crossProduct(Vec3i vec) {
        return this.crossProduct(vec);
    }

    @Override
    public /* synthetic */ Vec3i offset(Direction direction, int distance) {
        return this.offset(direction, distance);
    }

    @Override
    public /* synthetic */ Vec3i down(int i) {
        return this.down(i);
    }

    @Override
    public /* synthetic */ Vec3i down() {
        return this.down();
    }

    static {
        SIZE_BITS_Z = SIZE_BITS_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
        SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
        BITS_X = (1L << SIZE_BITS_X) - 1L;
        BITS_Y = (1L << SIZE_BITS_Y) - 1L;
        BITS_Z = (1L << SIZE_BITS_Z) - 1L;
        BIT_SHIFT_Z = SIZE_BITS_Y;
        BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;
    }

    public static class Mutable
    extends BlockPos {
        public Mutable() {
            this(0, 0, 0);
        }

        public Mutable(int i, int j, int k) {
            super(i, j, k);
        }

        public Mutable(double d, double e, double f) {
            this(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        }

        @Override
        public BlockPos add(double x, double y, double z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos add(int x, int y, int z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos offset(Direction arg, int i) {
            return super.offset(arg, i).toImmutable();
        }

        @Override
        public BlockPos method_30513(Direction.Axis arg, int i) {
            return super.method_30513(arg, i).toImmutable();
        }

        @Override
        public BlockPos rotate(BlockRotation rotation) {
            return super.rotate(rotation).toImmutable();
        }

        public Mutable set(int x, int y, int z) {
            this.setX(x);
            this.setY(y);
            this.setZ(z);
            return this;
        }

        public Mutable set(double x, double y, double z) {
            return this.set(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        }

        public Mutable set(Vec3i pos) {
            return this.set(pos.getX(), pos.getY(), pos.getZ());
        }

        public Mutable set(long pos) {
            return this.set(Mutable.unpackLongX(pos), Mutable.unpackLongY(pos), Mutable.unpackLongZ(pos));
        }

        public Mutable set(AxisCycleDirection axis, int x, int y, int z) {
            return this.set(axis.choose(x, y, z, Direction.Axis.X), axis.choose(x, y, z, Direction.Axis.Y), axis.choose(x, y, z, Direction.Axis.Z));
        }

        public Mutable set(Vec3i pos, Direction direction) {
            return this.set(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
        }

        public Mutable set(Vec3i pos, int x, int y, int z) {
            return this.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
        }

        public Mutable move(Direction direction) {
            return this.move(direction, 1);
        }

        public Mutable move(Direction direction, int distance) {
            return this.set(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance);
        }

        public Mutable move(int dx, int dy, int dz) {
            return this.set(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
        }

        public Mutable method_27158(Direction.Axis arg, int i, int j) {
            switch (arg) {
                case X: {
                    return this.set(MathHelper.clamp(this.getX(), i, j), this.getY(), this.getZ());
                }
                case Y: {
                    return this.set(this.getX(), MathHelper.clamp(this.getY(), i, j), this.getZ());
                }
                case Z: {
                    return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), i, j));
                }
            }
            throw new IllegalStateException("Unable to clamp axis " + arg);
        }

        @Override
        public void setX(int x) {
            super.setX(x);
        }

        @Override
        public void setY(int y) {
            super.setY(y);
        }

        @Override
        public void setZ(int z) {
            super.setZ(z);
        }

        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }

        @Override
        public /* synthetic */ Vec3i crossProduct(Vec3i vec) {
            return super.crossProduct(vec);
        }

        @Override
        public /* synthetic */ Vec3i offset(Direction direction, int distance) {
            return this.offset(direction, distance);
        }

        @Override
        public /* synthetic */ Vec3i down(int i) {
            return super.down(i);
        }

        @Override
        public /* synthetic */ Vec3i down() {
            return super.down();
        }
    }
}

