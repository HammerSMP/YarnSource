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
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new LandPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, range);
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
    public Path findPathTo(BlockPos target, int distance) {
        if (this.world.getBlockState(target).isAir()) {
            BlockPos lv = target.down();
            while (lv.getY() > 0 && this.world.getBlockState(lv).isAir()) {
                lv = lv.down();
            }
            if (lv.getY() > 0) {
                return super.findPathTo(lv.up(), distance);
            }
            while (lv.getY() < this.world.getHeight() && this.world.getBlockState(lv).isAir()) {
                lv = lv.up();
            }
            target = lv;
        }
        if (this.world.getBlockState(target).getMaterial().isSolid()) {
            BlockPos lv2 = target.up();
            while (lv2.getY() < this.world.getHeight() && this.world.getBlockState(lv2).getMaterial().isSolid()) {
                lv2 = lv2.up();
            }
            return super.findPathTo(lv2, distance);
        }
        return super.findPathTo(target, distance);
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(entity.getBlockPos(), distance);
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
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
        int l = MathHelper.floor(origin.x);
        int m = MathHelper.floor(origin.z);
        double d = target.x - origin.x;
        double e = target.z - origin.z;
        double f = d * d + e * e;
        if (f < 1.0E-8) {
            return false;
        }
        double g = 1.0 / Math.sqrt(f);
        if (!this.allVisibleAreSafe(l, MathHelper.floor(origin.y), m, sizeX += 2, sizeY, sizeZ += 2, origin, d *= g, e *= g)) {
            return false;
        }
        sizeX -= 2;
        sizeZ -= 2;
        double h = 1.0 / Math.abs(d);
        double n = 1.0 / Math.abs(e);
        double o = (double)l - origin.x;
        double p = (double)m - origin.z;
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
        int s = MathHelper.floor(target.x);
        int t = MathHelper.floor(target.z);
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
            if (this.allVisibleAreSafe(l, MathHelper.floor(origin.y), m, sizeX, sizeY, sizeZ, origin, d, e)) continue;
            return false;
        }
        return true;
    }

    private boolean allVisibleAreSafe(int centerX, int centerY, int centerZ, int xSize, int ySize, int zSize, Vec3d entityPos, double lookVecX, double lookVecZ) {
        int o = centerX - xSize / 2;
        int p = centerZ - zSize / 2;
        if (!this.allVisibleArePassable(o, centerY, p, xSize, ySize, zSize, entityPos, lookVecX, lookVecZ)) {
            return false;
        }
        for (int q = o; q < o + xSize; ++q) {
            for (int r = p; r < p + zSize; ++r) {
                double f = (double)q + 0.5 - entityPos.x;
                double g = (double)r + 0.5 - entityPos.z;
                if (f * lookVecX + g * lookVecZ < 0.0) continue;
                PathNodeType lv = this.nodeMaker.getNodeType(this.world, q, centerY - 1, r, this.entity, xSize, ySize, zSize, true, true);
                if (!this.canWalkOnPath(lv)) {
                    return false;
                }
                lv = this.nodeMaker.getNodeType(this.world, q, centerY, r, this.entity, xSize, ySize, zSize, true, true);
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

    protected boolean canWalkOnPath(PathNodeType pathType) {
        if (pathType == PathNodeType.WATER) {
            return false;
        }
        if (pathType == PathNodeType.LAVA) {
            return false;
        }
        return pathType != PathNodeType.OPEN;
    }

    private boolean allVisibleArePassable(int x, int y, int z, int xSize, int ySize, int zSize, Vec3d entityPos, double lookVecX, double lookVecZ) {
        for (BlockPos lv : BlockPos.iterate(new BlockPos(x, y, z), new BlockPos(x + xSize - 1, y + ySize - 1, z + zSize - 1))) {
            double g;
            double f = (double)lv.getX() + 0.5 - entityPos.x;
            if (f * lookVecX + (g = (double)lv.getZ() + 0.5 - entityPos.z) * lookVecZ < 0.0 || this.world.getBlockState(lv).canPathfindThrough(this.world, lv, NavigationType.LAND)) continue;
            return false;
        }
        return true;
    }

    public void setCanPathThroughDoors(boolean canPathThroughDoors) {
        this.nodeMaker.setCanOpenDoors(canPathThroughDoors);
    }

    public boolean canEnterOpenDoors() {
        return this.nodeMaker.canEnterOpenDoors();
    }

    public void setAvoidSunlight(boolean avoidSunlight) {
        this.avoidSunlight = avoidSunlight;
    }
}

