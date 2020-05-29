/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.mojang.serialization.Codec
 *  javax.annotation.concurrent.Immutable
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos
extends Vec3i {
    public static final Codec<BlockPos> field_25064 = Codec.INT_STREAM.comapFlatMap(intStream -> Util.method_29190(intStream, 3).map(is -> new BlockPos(is[0], is[1], is[2])), arg -> IntStream.of(arg.getX(), arg.getY(), arg.getZ())).stable();
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

    public BlockPos(Vec3d arg) {
        this(arg.x, arg.y, arg.z);
    }

    public BlockPos(Position arg) {
        this(arg.getX(), arg.getY(), arg.getZ());
    }

    public BlockPos(Vec3i arg) {
        this(arg.getX(), arg.getY(), arg.getZ());
    }

    public static long offset(long l, Direction arg) {
        return BlockPos.add(l, arg.getOffsetX(), arg.getOffsetY(), arg.getOffsetZ());
    }

    public static long add(long l, int i, int j, int k) {
        return BlockPos.asLong(BlockPos.unpackLongX(l) + i, BlockPos.unpackLongY(l) + j, BlockPos.unpackLongZ(l) + k);
    }

    public static int unpackLongX(long l) {
        return (int)(l << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
    }

    public static int unpackLongY(long l) {
        return (int)(l << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
    }

    public static int unpackLongZ(long l) {
        return (int)(l << 64 - BIT_SHIFT_Z - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
    }

    public static BlockPos fromLong(long l) {
        return new BlockPos(BlockPos.unpackLongX(l), BlockPos.unpackLongY(l), BlockPos.unpackLongZ(l));
    }

    public long asLong() {
        return BlockPos.asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int i, int j, int k) {
        long l = 0L;
        l |= ((long)i & BITS_X) << BIT_SHIFT_X;
        l |= ((long)j & BITS_Y) << 0;
        return l |= ((long)k & BITS_Z) << BIT_SHIFT_Z;
    }

    public static long removeChunkSectionLocalY(long l) {
        return l & 0xFFFFFFFFFFFFFFF0L;
    }

    public BlockPos add(double d, double e, double f) {
        if (d == 0.0 && e == 0.0 && f == 0.0) {
            return this;
        }
        return new BlockPos((double)this.getX() + d, (double)this.getY() + e, (double)this.getZ() + f);
    }

    public BlockPos add(int i, int j, int k) {
        if (i == 0 && j == 0 && k == 0) {
            return this;
        }
        return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPos add(Vec3i arg) {
        return this.add(arg.getX(), arg.getY(), arg.getZ());
    }

    public BlockPos subtract(Vec3i arg) {
        return this.add(-arg.getX(), -arg.getY(), -arg.getZ());
    }

    public BlockPos up() {
        return this.offset(Direction.UP);
    }

    public BlockPos up(int i) {
        return this.offset(Direction.UP, i);
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

    public BlockPos north(int i) {
        return this.offset(Direction.NORTH, i);
    }

    public BlockPos south() {
        return this.offset(Direction.SOUTH);
    }

    public BlockPos south(int i) {
        return this.offset(Direction.SOUTH, i);
    }

    public BlockPos west() {
        return this.offset(Direction.WEST);
    }

    public BlockPos west(int i) {
        return this.offset(Direction.WEST, i);
    }

    public BlockPos east() {
        return this.offset(Direction.EAST);
    }

    public BlockPos east(int i) {
        return this.offset(Direction.EAST, i);
    }

    public BlockPos offset(Direction arg) {
        return new BlockPos(this.getX() + arg.getOffsetX(), this.getY() + arg.getOffsetY(), this.getZ() + arg.getOffsetZ());
    }

    @Override
    public BlockPos offset(Direction arg, int i) {
        if (i == 0) {
            return this;
        }
        return new BlockPos(this.getX() + arg.getOffsetX() * i, this.getY() + arg.getOffsetY() * i, this.getZ() + arg.getOffsetZ() * i);
    }

    public BlockPos rotate(BlockRotation arg) {
        switch (arg) {
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
    public BlockPos crossProduct(Vec3i arg) {
        return new BlockPos(this.getY() * arg.getZ() - this.getZ() * arg.getY(), this.getZ() * arg.getX() - this.getX() * arg.getZ(), this.getX() * arg.getY() - this.getY() * arg.getX());
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

    public static Iterable<BlockPos> iterateOutwards(BlockPos arg, final int i, final int j, final int k) {
        final int l = i + j + k;
        final int m = arg.getX();
        final int n = arg.getY();
        final int o = arg.getZ();
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
                            this.limitX = Math.min(i, this.manhattanDistance);
                            this.dx = -this.limitX;
                        }
                        this.limitY = Math.min(j, this.manhattanDistance - Math.abs(this.dx));
                        this.dy = -this.limitY;
                    }
                    int i2 = this.dx;
                    int j2 = this.dy;
                    int k2 = this.manhattanDistance - Math.abs(i2) - Math.abs(j2);
                    if (k2 <= k) {
                        this.field_23379 = k2 != 0;
                        lv = this.field_23378.set(m + i2, n + j2, o + k2);
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

    public static Optional<BlockPos> findClosest(BlockPos arg, int i, int j, Predicate<BlockPos> predicate) {
        return BlockPos.streamOutwards(arg, i, j, i).filter(predicate).findFirst();
    }

    public static Stream<BlockPos> streamOutwards(BlockPos arg, int i, int j, int k) {
        return StreamSupport.stream(BlockPos.iterateOutwards(arg, i, j, k).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(BlockPos arg, BlockPos arg2) {
        return BlockPos.iterate(Math.min(arg.getX(), arg2.getX()), Math.min(arg.getY(), arg2.getY()), Math.min(arg.getZ(), arg2.getZ()), Math.max(arg.getX(), arg2.getX()), Math.max(arg.getY(), arg2.getY()), Math.max(arg.getZ(), arg2.getZ()));
    }

    public static Stream<BlockPos> stream(BlockPos arg, BlockPos arg2) {
        return StreamSupport.stream(BlockPos.iterate(arg, arg2).spliterator(), false);
    }

    public static Stream<BlockPos> stream(BlockBox arg) {
        return BlockPos.stream(Math.min(arg.minX, arg.maxX), Math.min(arg.minY, arg.maxY), Math.min(arg.minZ, arg.maxZ), Math.max(arg.minX, arg.maxX), Math.max(arg.minY, arg.maxY), Math.max(arg.minZ, arg.maxZ));
    }

    public static Stream<BlockPos> stream(int i, int j, int k, int l, int m, int n) {
        return StreamSupport.stream(BlockPos.iterate(i, j, k, l, m, n).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(final int i, final int j, final int k, int l, int m, int n) {
        final int o = l - i + 1;
        final int p = m - j + 1;
        int q = n - k + 1;
        final int r = o * p * q;
        return () -> new AbstractIterator<BlockPos>(){
            private final Mutable field_23380 = new Mutable();
            private int index;

            protected BlockPos computeNext() {
                if (this.index == r) {
                    return (BlockPos)this.endOfData();
                }
                int i2 = this.index % o;
                int j2 = this.index / o;
                int k2 = j2 % p;
                int l = j2 / p;
                ++this.index;
                return this.field_23380.set(i + i2, j + k2, k + l);
            }

            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    @Override
    public /* synthetic */ Vec3i crossProduct(Vec3i arg) {
        return this.crossProduct(arg);
    }

    @Override
    public /* synthetic */ Vec3i offset(Direction arg, int i) {
        return this.offset(arg, i);
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
        public BlockPos add(double d, double e, double f) {
            return super.add(d, e, f).toImmutable();
        }

        @Override
        public BlockPos add(int i, int j, int k) {
            return super.add(i, j, k).toImmutable();
        }

        @Override
        public BlockPos offset(Direction arg, int i) {
            return super.offset(arg, i).toImmutable();
        }

        @Override
        public BlockPos rotate(BlockRotation arg) {
            return super.rotate(arg).toImmutable();
        }

        public Mutable set(int i, int j, int k) {
            this.setX(i);
            this.setY(j);
            this.setZ(k);
            return this;
        }

        public Mutable set(double d, double e, double f) {
            return this.set(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        }

        public Mutable set(Vec3i arg) {
            return this.set(arg.getX(), arg.getY(), arg.getZ());
        }

        public Mutable set(long l) {
            return this.set(Mutable.unpackLongX(l), Mutable.unpackLongY(l), Mutable.unpackLongZ(l));
        }

        public Mutable set(AxisCycleDirection arg, int i, int j, int k) {
            return this.set(arg.choose(i, j, k, Direction.Axis.X), arg.choose(i, j, k, Direction.Axis.Y), arg.choose(i, j, k, Direction.Axis.Z));
        }

        public Mutable set(Vec3i arg, Direction arg2) {
            return this.set(arg.getX() + arg2.getOffsetX(), arg.getY() + arg2.getOffsetY(), arg.getZ() + arg2.getOffsetZ());
        }

        public Mutable set(Vec3i arg, int i, int j, int k) {
            return this.set(arg.getX() + i, arg.getY() + j, arg.getZ() + k);
        }

        public Mutable move(Direction arg) {
            return this.move(arg, 1);
        }

        public Mutable move(Direction arg, int i) {
            return this.set(this.getX() + arg.getOffsetX() * i, this.getY() + arg.getOffsetY() * i, this.getZ() + arg.getOffsetZ() * i);
        }

        public Mutable move(int i, int j, int k) {
            return this.set(this.getX() + i, this.getY() + j, this.getZ() + k);
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
        public void setX(int i) {
            super.setX(i);
        }

        @Override
        public void setY(int i) {
            super.setY(i);
        }

        @Override
        public void setZ(int i) {
            super.setZ(i);
        }

        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }

        @Override
        public /* synthetic */ Vec3i crossProduct(Vec3i arg) {
            return super.crossProduct(arg);
        }

        @Override
        public /* synthetic */ Vec3i offset(Direction arg, int i) {
            return this.offset(arg, i);
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

