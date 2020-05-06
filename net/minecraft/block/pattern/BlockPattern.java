/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nullable
 */
package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldView;

public class BlockPattern {
    private final Predicate<CachedBlockPosition>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;

    public BlockPattern(Predicate<CachedBlockPosition>[][][] predicates) {
        this.pattern = predicates;
        this.depth = predicates.length;
        if (this.depth > 0) {
            this.height = predicates[0].length;
            this.width = this.height > 0 ? predicates[0][0].length : 0;
        } else {
            this.height = 0;
            this.width = 0;
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Nullable
    private Result testTransform(BlockPos arg, Direction arg2, Direction arg3, LoadingCache<BlockPos, CachedBlockPosition> loadingCache) {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.depth; ++k) {
                    if (this.pattern[k][j][i].test((CachedBlockPosition)loadingCache.getUnchecked((Object)BlockPattern.translate(arg, arg2, arg3, i, j, k)))) continue;
                    return null;
                }
            }
        }
        return new Result(arg, arg2, arg3, loadingCache, this.width, this.height, this.depth);
    }

    @Nullable
    public Result searchAround(WorldView arg, BlockPos arg2) {
        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(arg, false);
        int i = Math.max(Math.max(this.width, this.height), this.depth);
        for (BlockPos lv : BlockPos.iterate(arg2, arg2.add(i - 1, i - 1, i - 1))) {
            for (Direction lv2 : Direction.values()) {
                for (Direction lv3 : Direction.values()) {
                    Result lv4;
                    if (lv3 == lv2 || lv3 == lv2.getOpposite() || (lv4 = this.testTransform(lv, lv2, lv3, loadingCache)) == null) continue;
                    return lv4;
                }
            }
        }
        return null;
    }

    public static LoadingCache<BlockPos, CachedBlockPosition> makeCache(WorldView arg, boolean bl) {
        return CacheBuilder.newBuilder().build((CacheLoader)new BlockStateCacheLoader(arg, bl));
    }

    protected static BlockPos translate(BlockPos arg, Direction arg2, Direction arg3, int i, int j, int k) {
        if (arg2 == arg3 || arg2 == arg3.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        Vec3i lv = new Vec3i(arg2.getOffsetX(), arg2.getOffsetY(), arg2.getOffsetZ());
        Vec3i lv2 = new Vec3i(arg3.getOffsetX(), arg3.getOffsetY(), arg3.getOffsetZ());
        Vec3i lv3 = lv.crossProduct(lv2);
        return arg.add(lv2.getX() * -j + lv3.getX() * i + lv.getX() * k, lv2.getY() * -j + lv3.getY() * i + lv.getY() * k, lv2.getZ() * -j + lv3.getZ() * i + lv.getZ() * k);
    }

    public static class TeleportTarget {
        public final Vec3d pos;
        public final Vec3d velocity;
        public final int yaw;

        public TeleportTarget(Vec3d arg, Vec3d arg2, int i) {
            this.pos = arg;
            this.velocity = arg2;
            this.yaw = i;
        }
    }

    public static class Result {
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, CachedBlockPosition> cache;
        private final int width;
        private final int height;
        private final int depth;

        public Result(BlockPos arg, Direction arg2, Direction arg3, LoadingCache<BlockPos, CachedBlockPosition> loadingCache, int i, int j, int k) {
            this.frontTopLeft = arg;
            this.forwards = arg2;
            this.up = arg3;
            this.cache = loadingCache;
            this.width = i;
            this.height = j;
            this.depth = k;
        }

        public BlockPos getFrontTopLeft() {
            return this.frontTopLeft;
        }

        public Direction getForwards() {
            return this.forwards;
        }

        public Direction getUp() {
            return this.up;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public CachedBlockPosition translate(int i, int j, int k) {
            return (CachedBlockPosition)this.cache.getUnchecked((Object)BlockPattern.translate(this.frontTopLeft, this.getForwards(), this.getUp(), i, j, k));
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("up", (Object)this.up).add("forwards", (Object)this.forwards).add("frontTopLeft", (Object)this.frontTopLeft).toString();
        }

        public TeleportTarget getTeleportTarget(Direction arg, BlockPos arg2, double d, Vec3d arg3, double e) {
            double v;
            double u;
            double n;
            double m;
            Direction lv = this.getForwards();
            Direction lv2 = lv.rotateYClockwise();
            double f = (double)(this.getFrontTopLeft().getY() + 1) - d * (double)this.getHeight();
            if (lv2 == Direction.NORTH) {
                double g = (double)arg2.getX() + 0.5;
                double h = (double)(this.getFrontTopLeft().getZ() + 1) - (1.0 - e) * (double)this.getWidth();
            } else if (lv2 == Direction.SOUTH) {
                double i = (double)arg2.getX() + 0.5;
                double j = (double)this.getFrontTopLeft().getZ() + (1.0 - e) * (double)this.getWidth();
            } else if (lv2 == Direction.WEST) {
                double k = (double)(this.getFrontTopLeft().getX() + 1) - (1.0 - e) * (double)this.getWidth();
                double l = (double)arg2.getZ() + 0.5;
            } else {
                m = (double)this.getFrontTopLeft().getX() + (1.0 - e) * (double)this.getWidth();
                n = (double)arg2.getZ() + 0.5;
            }
            if (lv.getOpposite() == arg) {
                double o = arg3.x;
                double p = arg3.z;
            } else if (lv.getOpposite() == arg.getOpposite()) {
                double q = -arg3.x;
                double r = -arg3.z;
            } else if (lv.getOpposite() == arg.rotateYClockwise()) {
                double s = -arg3.z;
                double t = arg3.x;
            } else {
                u = arg3.z;
                v = -arg3.x;
            }
            int w = (lv.getHorizontal() - arg.getOpposite().getHorizontal()) * 90;
            return new TeleportTarget(new Vec3d(m, f, n), new Vec3d(u, arg3.y, v), w);
        }
    }

    static class BlockStateCacheLoader
    extends CacheLoader<BlockPos, CachedBlockPosition> {
        private final WorldView world;
        private final boolean forceLoad;

        public BlockStateCacheLoader(WorldView arg, boolean bl) {
            this.world = arg;
            this.forceLoad = bl;
        }

        public CachedBlockPosition load(BlockPos arg) throws Exception {
            return new CachedBlockPosition(this.world, arg, this.forceLoad);
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((BlockPos)object);
        }
    }
}

