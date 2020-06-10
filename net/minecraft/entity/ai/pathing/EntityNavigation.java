/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;

public abstract class EntityNavigation {
    protected final MobEntity entity;
    protected final World world;
    @Nullable
    protected Path currentPath;
    protected double speed;
    protected int tickCount;
    protected int pathStartTime;
    protected Vec3d pathStartPos = Vec3d.ZERO;
    protected Vec3i lastNodePosition = Vec3i.ZERO;
    protected long currentNodeMs;
    protected long lastActiveTickMs;
    protected double currentNodeTimeout;
    protected float nodeReachProximity = 0.5f;
    protected boolean shouldRecalculate;
    protected long lastRecalculateTime;
    protected PathNodeMaker nodeMaker;
    private BlockPos currentTarget;
    private int currentDistance;
    private float rangeMultiplier = 1.0f;
    private final PathNodeNavigator pathNodeNavigator;

    public EntityNavigation(MobEntity arg, World arg2) {
        this.entity = arg;
        this.world = arg2;
        int i = MathHelper.floor(arg.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 16.0);
        this.pathNodeNavigator = this.createPathNodeNavigator(i);
    }

    public void resetRangeMultiplier() {
        this.rangeMultiplier = 1.0f;
    }

    public void setRangeMultiplier(float f) {
        this.rangeMultiplier = f;
    }

    public BlockPos getTargetPos() {
        return this.currentTarget;
    }

    protected abstract PathNodeNavigator createPathNodeNavigator(int var1);

    public void setSpeed(double d) {
        this.speed = d;
    }

    public boolean shouldRecalculatePath() {
        return this.shouldRecalculate;
    }

    public void recalculatePath() {
        if (this.world.getTime() - this.lastRecalculateTime > 20L) {
            if (this.currentTarget != null) {
                this.currentPath = null;
                this.currentPath = this.findPathTo(this.currentTarget, this.currentDistance);
                this.lastRecalculateTime = this.world.getTime();
                this.shouldRecalculate = false;
            }
        } else {
            this.shouldRecalculate = true;
        }
    }

    @Nullable
    public final Path findPathTo(double d, double e, double f, int i) {
        return this.findPathTo(new BlockPos(d, e, f), i);
    }

    @Nullable
    public Path findPathToAny(Stream<BlockPos> stream, int i) {
        return this.findPathToAny(stream.collect(Collectors.toSet()), 8, false, i);
    }

    @Nullable
    public Path method_29934(Set<BlockPos> set, int i) {
        return this.findPathToAny(set, 8, false, i);
    }

    @Nullable
    public Path findPathTo(BlockPos arg, int i) {
        return this.findPathToAny((Set<BlockPos>)ImmutableSet.of((Object)arg), 8, false, i);
    }

    @Nullable
    public Path findPathTo(Entity arg, int i) {
        return this.findPathToAny((Set<BlockPos>)ImmutableSet.of((Object)arg.getBlockPos()), 16, true, i);
    }

    @Nullable
    protected Path findPathToAny(Set<BlockPos> set, int i, boolean bl, int j) {
        if (set.isEmpty()) {
            return null;
        }
        if (this.entity.getY() < 0.0) {
            return null;
        }
        if (!this.isAtValidPosition()) {
            return null;
        }
        if (this.currentPath != null && !this.currentPath.isFinished() && set.contains(this.currentTarget)) {
            return this.currentPath;
        }
        this.world.getProfiler().push("pathfind");
        float f = (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        BlockPos lv = bl ? this.entity.getBlockPos().up() : this.entity.getBlockPos();
        int k = (int)(f + (float)i);
        ChunkCache lv2 = new ChunkCache(this.world, lv.add(-k, -k, -k), lv.add(k, k, k));
        Path lv3 = this.pathNodeNavigator.findPathToAny(lv2, this.entity, set, f, j, this.rangeMultiplier);
        this.world.getProfiler().pop();
        if (lv3 != null && lv3.getTarget() != null) {
            this.currentTarget = lv3.getTarget();
            this.currentDistance = j;
            this.method_26085();
        }
        return lv3;
    }

    public boolean startMovingTo(double d, double e, double f, double g) {
        return this.startMovingAlong(this.findPathTo(d, e, f, 1), g);
    }

    public boolean startMovingTo(Entity arg, double d) {
        Path lv = this.findPathTo(arg, 1);
        return lv != null && this.startMovingAlong(lv, d);
    }

    public boolean startMovingAlong(@Nullable Path arg, double d) {
        if (arg == null) {
            this.currentPath = null;
            return false;
        }
        if (!arg.equalsPath(this.currentPath)) {
            this.currentPath = arg;
        }
        if (this.isIdle()) {
            return false;
        }
        this.adjustPath();
        if (this.currentPath.getLength() <= 0) {
            return false;
        }
        this.speed = d;
        Vec3d lv = this.getPos();
        this.pathStartTime = this.tickCount;
        this.pathStartPos = lv;
        return true;
    }

    @Nullable
    public Path getCurrentPath() {
        return this.currentPath;
    }

    public void tick() {
        BlockPos lv4;
        Vec3d lv3;
        ++this.tickCount;
        if (this.shouldRecalculate) {
            this.recalculatePath();
        }
        if (this.isIdle()) {
            return;
        }
        if (this.isAtValidPosition()) {
            this.continueFollowingPath();
        } else if (this.currentPath != null && this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength()) {
            Vec3d lv = this.getPos();
            Vec3d lv2 = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
            if (lv.y > lv2.y && !this.entity.isOnGround() && MathHelper.floor(lv.x) == MathHelper.floor(lv2.x) && MathHelper.floor(lv.z) == MathHelper.floor(lv2.z)) {
                this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
            }
        }
        DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
        if (this.isIdle()) {
            return;
        }
        this.entity.getMoveControl().moveTo(lv3.x, this.world.getBlockState((lv4 = new BlockPos(lv3 = this.currentPath.getNodePosition(this.entity))).down()).isAir() ? lv3.y : LandPathNodeMaker.getFeetY(this.world, lv4), lv3.z, this.speed);
    }

    protected void continueFollowingPath() {
        boolean bl;
        Vec3d lv = this.getPos();
        this.nodeReachProximity = this.entity.getWidth() > 0.75f ? this.entity.getWidth() / 2.0f : 0.75f - this.entity.getWidth() / 2.0f;
        Vec3i lv2 = this.currentPath.getCurrentPosition();
        double d = Math.abs(this.entity.getX() - ((double)lv2.getX() + 0.5));
        double e = Math.abs(this.entity.getY() - (double)lv2.getY());
        double f = Math.abs(this.entity.getZ() - ((double)lv2.getZ() + 0.5));
        boolean bl2 = bl = d < (double)this.nodeReachProximity && f < (double)this.nodeReachProximity && e < 1.0;
        if (bl || this.entity.method_29244(this.currentPath.method_29301().type) && this.method_27799(lv)) {
            this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
        }
        this.checkTimeouts(lv);
    }

    private boolean method_27799(Vec3d arg) {
        Vec3d lv4;
        if (this.currentPath.getLength() <= this.currentPath.getCurrentNodeIndex() + 1) {
            return false;
        }
        Vec3d lv = Vec3d.ofBottomCenter(this.currentPath.getNode(this.currentPath.getCurrentNodeIndex()).getPos());
        if (!arg.isInRange(lv, 2.0)) {
            return false;
        }
        Vec3d lv2 = Vec3d.ofBottomCenter(this.currentPath.getNode(this.currentPath.getCurrentNodeIndex() + 1).getPos());
        Vec3d lv3 = lv2.subtract(lv);
        return lv3.dotProduct(lv4 = arg.subtract(lv)) > 0.0;
    }

    protected void checkTimeouts(Vec3d arg) {
        if (this.tickCount - this.pathStartTime > 100) {
            if (arg.squaredDistanceTo(this.pathStartPos) < 2.25) {
                this.stop();
            }
            this.pathStartTime = this.tickCount;
            this.pathStartPos = arg;
        }
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            Vec3i lv = this.currentPath.getCurrentPosition();
            if (lv.equals(this.lastNodePosition)) {
                this.currentNodeMs += Util.getMeasuringTimeMs() - this.lastActiveTickMs;
            } else {
                this.lastNodePosition = lv;
                double d = arg.distanceTo(Vec3d.ofBottomCenter(this.lastNodePosition));
                double d2 = this.currentNodeTimeout = this.entity.getMovementSpeed() > 0.0f ? d / (double)this.entity.getMovementSpeed() * 1000.0 : 0.0;
            }
            if (this.currentNodeTimeout > 0.0 && (double)this.currentNodeMs > this.currentNodeTimeout * 3.0) {
                this.method_26085();
                this.stop();
            }
            this.lastActiveTickMs = Util.getMeasuringTimeMs();
        }
    }

    private void method_26085() {
        this.lastNodePosition = Vec3i.ZERO;
        this.currentNodeMs = 0L;
        this.currentNodeTimeout = 0.0;
    }

    public boolean isIdle() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public boolean isFollowingPath() {
        return !this.isIdle();
    }

    public void stop() {
        this.currentPath = null;
    }

    protected abstract Vec3d getPos();

    protected abstract boolean isAtValidPosition();

    protected boolean isInLiquid() {
        return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isInLava();
    }

    protected void adjustPath() {
        if (this.currentPath == null) {
            return;
        }
        for (int i = 0; i < this.currentPath.getLength(); ++i) {
            PathNode lv = this.currentPath.getNode(i);
            PathNode lv2 = i + 1 < this.currentPath.getLength() ? this.currentPath.getNode(i + 1) : null;
            BlockState lv3 = this.world.getBlockState(new BlockPos(lv.x, lv.y, lv.z));
            if (!lv3.isOf(Blocks.CAULDRON)) continue;
            this.currentPath.setNode(i, lv.copyWithNewPosition(lv.x, lv.y + 1, lv.z));
            if (lv2 == null || lv.y < lv2.y) continue;
            this.currentPath.setNode(i + 1, lv2.copyWithNewPosition(lv2.x, lv.y + 1, lv2.z));
        }
    }

    protected abstract boolean canPathDirectlyThrough(Vec3d var1, Vec3d var2, int var3, int var4, int var5);

    public boolean isValidPosition(BlockPos arg) {
        BlockPos lv = arg.down();
        return this.world.getBlockState(lv).isOpaqueFullCube(this.world, lv);
    }

    public PathNodeMaker getNodeMaker() {
        return this.nodeMaker;
    }

    public void setCanSwim(boolean bl) {
        this.nodeMaker.setCanSwim(bl);
    }

    public boolean canSwim() {
        return this.nodeMaker.canSwim();
    }

    public void onBlockChanged(BlockPos arg) {
        if (this.currentPath == null || this.currentPath.isFinished() || this.currentPath.getLength() == 0) {
            return;
        }
        PathNode lv = this.currentPath.getEnd();
        Vec3d lv2 = new Vec3d(((double)lv.x + this.entity.getX()) / 2.0, ((double)lv.y + this.entity.getY()) / 2.0, ((double)lv.z + this.entity.getZ()) / 2.0);
        if (arg.isWithinDistance(lv2, (double)(this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex()))) {
            this.recalculatePath();
        }
    }
}

