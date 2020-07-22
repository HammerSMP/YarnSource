/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpiderNavigation
extends MobNavigation {
    private BlockPos targetPos;

    public SpiderNavigation(MobEntity arg, World arg2) {
        super(arg, arg2);
    }

    @Override
    public Path findPathTo(BlockPos target, int distance) {
        this.targetPos = target;
        return super.findPathTo(target, distance);
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
        this.targetPos = entity.getBlockPos();
        return super.findPathTo(entity, distance);
    }

    @Override
    public boolean startMovingTo(Entity entity, double speed) {
        Path lv = this.findPathTo(entity, 0);
        if (lv != null) {
            return this.startMovingAlong(lv, speed);
        }
        this.targetPos = entity.getBlockPos();
        this.speed = speed;
        return true;
    }

    @Override
    public void tick() {
        if (this.isIdle()) {
            if (this.targetPos != null) {
                if (this.targetPos.isWithinDistance(this.entity.getPos(), (double)this.entity.getWidth()) || this.entity.getY() > (double)this.targetPos.getY() && new BlockPos((double)this.targetPos.getX(), this.entity.getY(), (double)this.targetPos.getZ()).isWithinDistance(this.entity.getPos(), (double)this.entity.getWidth())) {
                    this.targetPos = null;
                } else {
                    this.entity.getMoveControl().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.speed);
                }
            }
            return;
        }
        super.tick();
    }
}

