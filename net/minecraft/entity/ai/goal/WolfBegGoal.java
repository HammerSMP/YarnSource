/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WolfBegGoal
extends Goal {
    private final WolfEntity wolf;
    private PlayerEntity begFrom;
    private final World world;
    private final float begDistance;
    private int timer;
    private final TargetPredicate validPlayerPredicate;

    public WolfBegGoal(WolfEntity arg, float f) {
        this.wolf = arg;
        this.world = arg.world;
        this.begDistance = f;
        this.validPlayerPredicate = new TargetPredicate().setBaseMaxDistance(f).includeInvulnerable().includeTeammates().ignoreEntityTargetRules();
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.begFrom = this.world.getClosestPlayer(this.validPlayerPredicate, this.wolf);
        if (this.begFrom == null) {
            return false;
        }
        return this.isAttractive(this.begFrom);
    }

    @Override
    public boolean shouldContinue() {
        if (!this.begFrom.isAlive()) {
            return false;
        }
        if (this.wolf.squaredDistanceTo(this.begFrom) > (double)(this.begDistance * this.begDistance)) {
            return false;
        }
        return this.timer > 0 && this.isAttractive(this.begFrom);
    }

    @Override
    public void start() {
        this.wolf.setBegging(true);
        this.timer = 40 + this.wolf.getRandom().nextInt(40);
    }

    @Override
    public void stop() {
        this.wolf.setBegging(false);
        this.begFrom = null;
    }

    @Override
    public void tick() {
        this.wolf.getLookControl().lookAt(this.begFrom.getX(), this.begFrom.getEyeY(), this.begFrom.getZ(), 10.0f, this.wolf.getLookPitchSpeed());
        --this.timer;
    }

    private boolean isAttractive(PlayerEntity arg) {
        for (Hand lv : Hand.values()) {
            ItemStack lv2 = arg.getStackInHand(lv);
            if (this.wolf.isTamed() && lv2.getItem() == Items.BONE) {
                return true;
            }
            if (!this.wolf.isBreedingItem(lv2)) continue;
            return true;
        }
        return false;
    }
}

