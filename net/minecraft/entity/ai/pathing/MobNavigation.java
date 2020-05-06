/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobNavigation
extends EntityNavigation {
    private boolean avoidSunlight;

    public MobNavigation(MobEntity arg, World arg2) {
        super(arg, arg2);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int i) {
        this.nodeMaker = new LandPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, i);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.entity.isOnGround() || this.isInLiquid() || this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.getPathfindingY(), this.entity.getZ());
    }

    @Override
    public Path findPathTo(BlockPos arg, int i) {
        if (this.world.getBlockState(arg).isAir()) {
            BlockPos lv = arg.down();
            while (lv.getY() > 0 && this.world.getBlockState(lv).isAir()) {
                lv = lv.down();
            }
            if (lv.getY() > 0) {
                return super.findPathTo(lv.up(), i);
            }
            while (lv.getY() < this.world.getHeight() && this.world.getBlockState(lv).isAir()) {
                lv = lv.up();
            }
            arg = lv;
        }
        if (this.world.getBlockState(arg).getMaterial().isSolid()) {
            BlockPos lv2 = arg.up();
            while (lv2.getY() < this.world.getHeight() && this.world.getBlockState(lv2).getMaterial().isSolid()) {
                lv2 = lv2.up();
            }
            return super.findPathTo(lv2, i);
        }
        return super.findPathTo(arg, i);
    }

    @Override
    public Path findPathTo(Entity arg, int i) {
        return this.findPathTo(arg.getBlockPos(), i);
    }

    private int getPathfindingY() {
        if (!this.entity.isTouchingWater() || !this.canSwim()) {
            return MathHelper.floor(this.entity.getY() + 0.5);
        }
        int i = MathHelper.floor(this.entity.getY());
        Block lv = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)i, this.entity.getZ())).getBlock();
        int j = 0;
        while (lv == Blocks.WATER) {
            lv = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)(++i), this.entity.getZ())).getBlock();
            if (++j <= 16) continue;
            return MathHelper.floor(this.entity.getY());
        }
        return i;
    }

    @Override
    protected void adjustPath() {
        super.adjustPath();
        if (this.avoidSunlight) {
            if (this.world.isSkyVisible(new BlockPos(this.entity.getX(), this.entity.getY() + 0.5, this.entity.getZ()))) {
                return;
            }
            for (int i = 0; i < this.currentPath.getLength(); ++i) {
                PathNode lv = this.currentPath.getNode(i);
                if (!this.world.isSkyVisible(new BlockPos(lv.x, lv.y, lv.z))) continue;
                this.currentPath.setLength(i);
                return;
            }
        }
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d arg, Vec3d arg2, int i, int j, int k) {
        int l = MathHelper.floor(arg.x);
        int m = MathHelper.floor(arg.z);
        double d = arg2.x - arg.x;
        double e = arg2.z - arg.z;
        double f = d * d + e * e;
        if (f < 1.0E-8) {
            return false;
        }
        double g = 1.0 / Math.sqrt(f);
        if (!this.allVisibleAreSafe(l, MathHelper.floor(arg.y), m, i += 2, j, k += 2, arg, d *= g, e *= g)) {
            return false;
        }
        i -= 2;
        k -= 2;
        double h = 1.0 / Math.abs(d);
        double n = 1.0 / Math.abs(e);
        double o = (double)l - arg.x;
        double p = (double)m - arg.z;
        if (d >= 0.0) {
            o += 1.0;
        }
        if (e >= 0.0) {
            p += 1.0;
        }
        o /= d;
        p /= e;
        int q = d < 0.0 ? -1 : 1;
        int r = e < 0.0 ? -1 : 1;
        int s = MathHelper.floor(arg2.x);
        int t = MathHelper.floor(arg2.z);
        int u = s - l;
        int v = t - m;
        while (u * q > 0 || v * r > 0) {
            if (o < p) {
                o += h;
                u = s - (l += q);
            } else {
                p += n;
                v = t - (m += r);
            }
            if (this.allVisibleAreSafe(l, MathHelper.floor(arg.y), m, i, j, k, arg, d, e)) continue;
            return false;
        }
        return true;
    }

    private boolean allVisibleAreSafe(int i, int j, int k, int l, int m, int n, Vec3d arg, double d, double e) {
        int o = i - l / 2;
        int p = k - n / 2;
        if (!this.allVisibleArePassable(o, j, p, l, m, n, arg, d, e)) {
            return false;
        }
        for (int q = o; q < o + l; ++q) {
            for (int r = p; r < p + n; ++r) {
                double f = (double)q + 0.5 - arg.x;
                double g = (double)r + 0.5 - arg.z;
                if (f * d + g * e < 0.0) continue;
                PathNodeType lv = this.nodeMaker.getNodeType(this.world, q, j - 1, r, this.entity, l, m, n, true, true);
                if (!this.canWalkOnPath(lv)) {
                    return false;
                }
                lv = this.nodeMaker.getNodeType(this.world, q, j, r, this.entity, l, m, n, true, true);
                float h = this.entity.getPathfindingPenalty(lv);
                if (h < 0.0f || h >= 8.0f) {
                    return false;
                }
                if (lv != PathNodeType.DAMAGE_FIRE && lv != PathNodeType.DANGER_FIRE && lv != PathNodeType.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }

    protected boolean canWalkOnPath(PathNodeType arg) {
        if (arg == PathNodeType.WATER) {
            return false;
        }
        if (arg == PathNodeType.LAVA) {
            return false;
        }
        return arg != PathNodeType.OPEN;
    }

    private boolean allVisibleArePassable(int i, int j, int k, int l, int m, int n, Vec3d arg, double d, double e) {
        for (BlockPos lv : BlockPos.iterate(new BlockPos(i, j, k), new BlockPos(i + l - 1, j + m - 1, k + n - 1))) {
            double g;
            double f = (double)lv.getX() + 0.5 - arg.x;
            if (f * d + (g = (double)lv.getZ() + 0.5 - arg.z) * e < 0.0 || this.world.getBlockState(lv).canPathfindThrough(this.world, lv, NavigationType.LAND)) continue;
            return false;
        }
        return true;
    }

    public void setCanPathThroughDoors(boolean bl) {
        this.nodeMaker.setCanOpenDoors(bl);
    }

    public boolean canEnterOpenDoors() {
        return this.nodeMaker.canEnterOpenDoors();
    }

    public void setAvoidSunlight(boolean bl) {
        this.avoidSunlight = bl;
    }
}

