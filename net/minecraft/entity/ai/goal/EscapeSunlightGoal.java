/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EscapeSunlightGoal
extends Goal {
    protected final MobEntityWithAi mob;
    private double targetX;
    private double targetY;
    private double targetZ;
    private final double speed;
    private final World world;

    public EscapeSunlightGoal(MobEntityWithAi arg, double d) {
        this.mob = arg;
        this.speed = d;
        this.world = arg.world;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.mob.getTarget() != null) {
            return false;
        }
        if (!this.world.isDay()) {
            return false;
        }
        if (!this.mob.isOnFire()) {
            return false;
        }
        if (!this.world.isSkyVisible(this.mob.getBlockPos())) {
            return false;
        }
        if (!this.mob.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            return false;
        }
        return this.targetShadedPos();
    }

    protected boolean targetShadedPos() {
        Vec3d lv = this.locateShadedPos();
        if (lv == null) {
            return false;
        }
        this.targetX = lv.x;
        this.targetY = lv.y;
        this.targetZ = lv.z;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Nullable
    protected Vec3d locateShadedPos() {
        Random random = this.mob.getRandom();
        BlockPos lv = this.mob.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            BlockPos lv2 = lv.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (this.world.isSkyVisible(lv2) || !(this.mob.getPathfindingFavor(lv2) < 0.0f)) continue;
            return Vec3d.method_24955(lv2);
        }
        return null;
    }
}

