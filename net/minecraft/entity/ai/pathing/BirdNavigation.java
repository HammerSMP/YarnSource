/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BirdNavigation
extends EntityNavigation {
    public BirdNavigation(MobEntity arg, World arg2) {
        super(arg, arg2);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int i) {
        this.nodeMaker = new BirdPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, i);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.canSwim() && this.isInLiquid() || !this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return this.entity.getPos();
    }

    @Override
    public Path findPathTo(Entity arg, int i) {
        return this.findPathTo(arg.getBlockPos(), i);
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
    protected boolean canPathDirectlyThrough(Vec3d arg, Vec3d arg2, int i, int j, int k) {
        int l = MathHelper.floor(arg.x);
        int m = MathHelper.floor(arg.y);
        int n = MathHelper.floor(arg.z);
        double d = arg2.x - arg.x;
        double e = arg2.y - arg.y;
        double f = arg2.z - arg.z;
        double g = d * d + e * e + f * f;
        if (g < 1.0E-8) {
            return false;
        }
        double h = 1.0 / Math.sqrt(g);
        double o = 1.0 / Math.abs(d *= h);
        double p = 1.0 / Math.abs(e *= h);
        double q = 1.0 / Math.abs(f *= h);
        double r = (double)l - arg.x;
        double s = (double)m - arg.y;
        double t = (double)n - arg.z;
        if (d >= 0.0) {
            r += 1.0;
        }
        if (e >= 0.0) {
            s += 1.0;
        }
        if (f >= 0.0) {
            t += 1.0;
        }
        r /= d;
        s /= e;
        t /= f;
        int u = d < 0.0 ? -1 : 1;
        int v = e < 0.0 ? -1 : 1;
        int w = f < 0.0 ? -1 : 1;
        int x = MathHelper.floor(arg2.x);
        int y = MathHelper.floor(arg2.y);
        int z = MathHelper.floor(arg2.z);
        int aa = x - l;
        int ab = y - m;
        int ac = z - n;
        while (aa * u > 0 || ab * v > 0 || ac * w > 0) {
            if (r < t && r <= s) {
                r += o;
                aa = x - (l += u);
                continue;
            }
            if (s < r && s <= t) {
                s += p;
                ab = y - (m += v);
                continue;
            }
            t += q;
            ac = z - (n += w);
        }
        return true;
    }

    public void setCanPathThroughDoors(boolean bl) {
        this.nodeMaker.setCanOpenDoors(bl);
    }

    public void setCanEnterOpenDoors(boolean bl) {
        this.nodeMaker.setCanEnterOpenDoors(bl);
    }

    @Override
    public boolean isValidPosition(BlockPos arg) {
        return this.world.getBlockState(arg).hasSolidTopSurface(this.world, arg, this.entity);
    }
}

