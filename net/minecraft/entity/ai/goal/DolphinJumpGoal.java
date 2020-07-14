/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DolphinJumpGoal
extends DiveJumpingGoal {
    private static final int[] OFFSET_MULTIPLIERS = new int[]{0, 1, 4, 5, 6, 7};
    private final DolphinEntity dolphin;
    private final int chance;
    private boolean inWater;

    public DolphinJumpGoal(DolphinEntity dolphin, int chance) {
        this.dolphin = dolphin;
        this.chance = chance;
    }

    @Override
    public boolean canStart() {
        if (this.dolphin.getRandom().nextInt(this.chance) != 0) {
            return false;
        }
        Direction lv = this.dolphin.getMovementDirection();
        int i = lv.getOffsetX();
        int j = lv.getOffsetZ();
        BlockPos lv2 = this.dolphin.getBlockPos();
        for (int k : OFFSET_MULTIPLIERS) {
            if (this.isWater(lv2, i, j, k) && this.isAirAbove(lv2, i, j, k)) continue;
            return false;
        }
        return true;
    }

    private boolean isWater(BlockPos pos, int xOffset, int zOffset, int multiplier) {
        BlockPos lv = pos.add(xOffset * multiplier, 0, zOffset * multiplier);
        return this.dolphin.world.getFluidState(lv).isIn(FluidTags.WATER) && !this.dolphin.world.getBlockState(lv).getMaterial().blocksMovement();
    }

    private boolean isAirAbove(BlockPos pos, int xOffset, int zOffset, int multiplier) {
        return this.dolphin.world.getBlockState(pos.add(xOffset * multiplier, 1, zOffset * multiplier)).isAir() && this.dolphin.world.getBlockState(pos.add(xOffset * multiplier, 2, zOffset * multiplier)).isAir();
    }

    @Override
    public boolean shouldContinue() {
        double d = this.dolphin.getVelocity().y;
        return !(d * d < (double)0.03f && this.dolphin.pitch != 0.0f && Math.abs(this.dolphin.pitch) < 10.0f && this.dolphin.isTouchingWater() || this.dolphin.isOnGround());
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void start() {
        Direction lv = this.dolphin.getMovementDirection();
        this.dolphin.setVelocity(this.dolphin.getVelocity().add((double)lv.getOffsetX() * 0.6, 0.7, (double)lv.getOffsetZ() * 0.6));
        this.dolphin.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.dolphin.pitch = 0.0f;
    }

    @Override
    public void tick() {
        boolean bl = this.inWater;
        if (!bl) {
            FluidState lv = this.dolphin.world.getFluidState(this.dolphin.getBlockPos());
            this.inWater = lv.isIn(FluidTags.WATER);
        }
        if (this.inWater && !bl) {
            this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0f, 1.0f);
        }
        Vec3d lv2 = this.dolphin.getVelocity();
        if (lv2.y * lv2.y < (double)0.03f && this.dolphin.pitch != 0.0f) {
            this.dolphin.pitch = MathHelper.lerpAngle(this.dolphin.pitch, 0.0f, 0.2f);
        } else {
            double d = Math.sqrt(Entity.squaredHorizontalLength(lv2));
            double e = Math.signum(-lv2.y) * Math.acos(d / lv2.length()) * 57.2957763671875;
            this.dolphin.pitch = (float)e;
        }
    }
}

