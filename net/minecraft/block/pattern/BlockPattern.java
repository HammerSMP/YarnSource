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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldView;

public class BlockPattern {
    private final Predicate<CachedBlockPosition>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;

    public BlockPattern(Predicate<CachedBlockPosition>[][][] pattern) {
        this.pattern = pattern;
        this.depth = pattern.length;
        if (this.depth > 0) {
            this.height = pattern[0].length;
            this.width = this.height > 0 ? pattern[0][0].length : 0;
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
    private Result testTransform(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache) {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.depth; ++k) {
                    if (this.pattern[k][j][i].test((CachedBlockPosition)cache.getUnchecked((Object)BlockPattern.translate(frontTopLeft, forwards, up, i, j, k)))) continue;
                    return null;
                }
            }
        }
        return new Result(frontTopLeft, forwards, up, cache, this.width, this.height, this.depth);
    }

    @Nullable
    public Result searchAround(WorldView world, BlockPos pos) {
        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(world, false);
        int i = Math.max(Math.max(this.width, this.height), this.depth);
        for (BlockPos lv : BlockPos.iterate(pos, pos.add(i - 1, i - 1, i - 1))) {
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

    public static LoadingCache<BlockPos, CachedBlockPosition> makeCache(WorldView world, boolean forceLoad) {
        return CacheBuilder.newBuilder().build((CacheLoader)new BlockStateCacheLoader(world, forceLoad));
    }

    protected static BlockPos translate(BlockPos pos, Direction forwards, Direction up, int offsetLeft, int offsetDown, int offsetForwards) {
        if (forwards == up || forwards == up.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        Vec3i lv = new Vec3i(forwards.getOffsetX(), forwards.getOffsetY(), forwards.getOffsetZ());
        Vec3i lv2 = new Vec3i(up.getOffsetX(), up.getOffsetY(), up.getOffsetZ());
        Vec3i lv3 = lv.crossProduct(lv2);
        return pos.add(lv2.getX() * -offsetDown + lv3.getX() * offsetLeft + lv.getX() * offsetForwards, lv2.getY() * -offsetDown + lv3.getY() * offsetLeft + lv.getY() * offsetForwards, lv2.getZ() * -offsetDown + lv3.getZ() * offsetLeft + lv.getZ() * offsetForwards);
    }

    public static class Result {
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, CachedBlockPosition> cache;
        private final int width;
        private final int height;
        private final int depth;

        public Result(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache, int width, int height, int depth) {
            this.frontTopLeft = frontTopLeft;
            this.forwards = forwards;
            this.up = up;
            this.cache = cache;
            this.width = width;
            this.height = height;
            this.depth = depth;
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

        public CachedBlockPosition translate(int i, int j, int k) {
            return (CachedBlockPosition)this.cache.getUnchecked((Object)BlockPattern.translate(this.frontTopLeft, this.getForwards(), this.getUp(), i, j, k));
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("up", (Object)this.up).add("forwards", (Object)this.forwards).add("frontTopLeft", (Object)this.frontTopLeft).toString();
        }
    }

    static class BlockStateCacheLoader
    extends CacheLoader<BlockPos, CachedBlockPosition> {
        private final WorldView world;
        private final boolean forceLoad;

        public BlockStateCacheLoader(WorldView world, boolean forceLoad) {
            this.world = world;
            this.forceLoad = forceLoad;
        }

        public CachedBlockPosition load(BlockPos arg) throws Exception {
            return new CachedBlockPosition(this.world, arg, this.forceLoad);
        }

        public /* synthetic */ Object load(Object pos) throws Exception {
            return this.load((BlockPos)pos);
        }
    }
}

