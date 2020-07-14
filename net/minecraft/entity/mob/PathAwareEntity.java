/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class PathAwareEntity
extends MobEntity {
    protected PathAwareEntity(EntityType<? extends PathAwareEntity> arg, World arg2) {
        super((EntityType<? extends MobEntity>)arg, arg2);
    }

    public float getPathfindingFavor(BlockPos pos) {
        return this.getPathfindingFavor(pos, this.world);
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0f;
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return this.getPathfindingFavor(this.getBlockPos(), world) >= 0.0f;
    }

    public boolean isNavigating() {
        return !this.getNavigation().isIdle();
    }

    @Override
    protected void updateLeash() {
        super.updateLeash();
        Entity lv = this.getHoldingEntity();
        if (lv != null && lv.world == this.world) {
            this.setPositionTarget(lv.getBlockPos(), 5);
            float f = this.distanceTo(lv);
            if (this instanceof TameableEntity && ((TameableEntity)this).isInSittingPose()) {
                if (f > 10.0f) {
                    this.detachLeash(true, true);
                }
                return;
            }
            this.updateForLeashLength(f);
            if (f > 10.0f) {
                this.detachLeash(true, true);
                this.goalSelector.disableControl(Goal.Control.MOVE);
            } else if (f > 6.0f) {
                double d = (lv.getX() - this.getX()) / (double)f;
                double e = (lv.getY() - this.getY()) / (double)f;
                double g = (lv.getZ() - this.getZ()) / (double)f;
                this.setVelocity(this.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
            } else {
                this.goalSelector.enableControl(Goal.Control.MOVE);
                float h = 2.0f;
                Vec3d lv2 = new Vec3d(lv.getX() - this.getX(), lv.getY() - this.getY(), lv.getZ() - this.getZ()).normalize().multiply(Math.max(f - 2.0f, 0.0f));
                this.getNavigation().startMovingTo(this.getX() + lv2.x, this.getY() + lv2.y, this.getZ() + lv2.z, this.getRunFromLeashSpeed());
            }
        }
    }

    protected double getRunFromLeashSpeed() {
        return 1.0;
    }

    protected void updateForLeashLength(float leashLength) {
    }
}

