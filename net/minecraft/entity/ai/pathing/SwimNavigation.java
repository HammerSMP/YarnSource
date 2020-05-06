/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.WaterPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class SwimNavigation
extends EntityNavigation {
    private boolean canJumpOutOfWater;

    public SwimNavigation(MobEntity arg, World arg2) {
        super(arg, arg2);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int i) {
        this.canJumpOutOfWater = this.entity instanceof DolphinEntity;
        this.nodeMaker = new WaterPathNodeMaker(this.canJumpOutOfWater);
        return new PathNodeNavigator(this.nodeMaker, i);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.canJumpOutOfWater || this.isInLiquid();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.entity.getBodyY(0.5), this.entity.getZ());
    }

    @Override
    public void tick() {
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
            Vec3d lv = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
            if (MathHelper.floor(this.entity.getX()) == MathHelper.floor(lv.x) && MathHelper.floor(this.entity.getY()) == MathHelper.floor(lv.y) && MathHelper.floor(this.entity.getZ()) == MathHelper.floor(lv.z)) {
                this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
            }
        }
        DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
        if (this.isIdle()) {
            return;
        }
        Vec3d lv2 = this.currentPath.getNodePosition(this.entity);
        this.entity.getMoveControl().moveTo(lv2.x, lv2.y, lv2.z, this.speed);
    }

    @Override
    protected void continueFollowingPath() {
        if (this.currentPath == null) {
            return;
        }
        Vec3d lv = this.getPos();
        float f = this.entity.getWidth();
        float g = f > 0.75f ? f / 2.0f : 0.75f - f / 2.0f;
        Vec3d lv2 = this.entity.getVelocity();
        if (Math.abs(lv2.x) > 0.2 || Math.abs(lv2.z) > 0.2) {
            g = (float)((double)g * (lv2.length() * 6.0));
        }
        int i = 6;
        Vec3d lv3 = Vec3d.method_24953(this.currentPath.getCurrentPosition());
        if (Math.abs(this.entity.getX() - (lv3.x + 0.5)) < (double)g && Math.abs(this.entity.getZ() - (lv3.z + 0.5)) < (double)g && Math.abs(this.entity.getY() - lv3.y) < (double)(g * 2.0f)) {
            this.currentPath.next();
        }
        for (int j = Math.min(this.currentPath.getCurrentNodeIndex() + 6, this.currentPath.getLength() - 1); j > this.currentPath.getCurrentNodeIndex(); --j) {
            lv3 = this.currentPath.getNodePosition(this.entity, j);
            if (lv3.squaredDistanceTo(lv) > 36.0 || !this.canPathDirectlyThrough(lv, lv3, 0, 0, 0)) continue;
            this.currentPath.setCurrentNodeIndex(j);
            break;
        }
        this.checkTimeouts(lv);
    }

    @Override
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
                double d = arg.distanceTo(Vec3d.method_24953(this.lastNodePosition));
                double d2 = this.currentNodeTimeout = this.entity.getMovementSpeed() > 0.0f ? d / (double)this.entity.getMovementSpeed() * 100.0 : 0.0;
            }
            if (this.currentNodeTimeout > 0.0 && (double)this.currentNodeMs > this.currentNodeTimeout * 2.0) {
                this.lastNodePosition = Vec3i.ZERO;
                this.currentNodeMs = 0L;
                this.currentNodeTimeout = 0.0;
                this.stop();
            }
            this.lastActiveTickMs = Util.getMeasuringTimeMs();
        }
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d arg, Vec3d arg2, int i, int j, int k) {
        Vec3d lv = new Vec3d(arg2.x, arg2.y + (double)this.entity.getHeight() * 0.5, arg2.z);
        return this.world.rayTrace(new RayTraceContext(arg, lv, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this.entity)).getType() == HitResult.Type.MISS;
    }

    @Override
    public boolean isValidPosition(BlockPos arg) {
        return !this.world.getBlockState(arg).isOpaqueFullCube(this.world, arg);
    }

    @Override
    public void setCanSwim(boolean bl) {
    }
}

