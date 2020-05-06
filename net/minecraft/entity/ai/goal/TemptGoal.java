/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class TemptGoal
extends Goal {
    private static final TargetPredicate TEMPTING_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeInvulnerable().includeTeammates().ignoreEntityTargetRules().includeHidden();
    protected final MobEntityWithAi mob;
    private final double speed;
    private double lastPlayerX;
    private double lastPlayerY;
    private double lastPlayerZ;
    private double lastPlayerPitch;
    private double lastPlayerYaw;
    protected PlayerEntity closestPlayer;
    private int cooldown;
    private boolean active;
    private final Ingredient food;
    private final boolean canBeScared;

    public TemptGoal(MobEntityWithAi arg, double d, Ingredient arg2, boolean bl) {
        this(arg, d, bl, arg2);
    }

    public TemptGoal(MobEntityWithAi arg, double d, boolean bl, Ingredient arg2) {
        this.mob = arg;
        this.speed = d;
        this.food = arg2;
        this.canBeScared = bl;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(arg.getNavigation() instanceof MobNavigation) && !(arg.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.closestPlayer = this.mob.world.getClosestPlayer(TEMPTING_ENTITY_PREDICATE, this.mob);
        if (this.closestPlayer == null) {
            return false;
        }
        return this.isTempedBy(this.closestPlayer.getMainHandStack()) || this.isTempedBy(this.closestPlayer.getOffHandStack());
    }

    protected boolean isTempedBy(ItemStack arg) {
        return this.food.test(arg);
    }

    @Override
    public boolean shouldContinue() {
        if (this.canBeScared()) {
            if (this.mob.squaredDistanceTo(this.closestPlayer) < 36.0) {
                if (this.closestPlayer.squaredDistanceTo(this.lastPlayerX, this.lastPlayerY, this.lastPlayerZ) > 0.010000000000000002) {
                    return false;
                }
                if (Math.abs((double)this.closestPlayer.pitch - this.lastPlayerPitch) > 5.0 || Math.abs((double)this.closestPlayer.yaw - this.lastPlayerYaw) > 5.0) {
                    return false;
                }
            } else {
                this.lastPlayerX = this.closestPlayer.getX();
                this.lastPlayerY = this.closestPlayer.getY();
                this.lastPlayerZ = this.closestPlayer.getZ();
            }
            this.lastPlayerPitch = this.closestPlayer.pitch;
            this.lastPlayerYaw = this.closestPlayer.yaw;
        }
        return this.canStart();
    }

    protected boolean canBeScared() {
        return this.canBeScared;
    }

    @Override
    public void start() {
        this.lastPlayerX = this.closestPlayer.getX();
        this.lastPlayerY = this.closestPlayer.getY();
        this.lastPlayerZ = this.closestPlayer.getZ();
        this.active = true;
    }

    @Override
    public void stop() {
        this.closestPlayer = null;
        this.mob.getNavigation().stop();
        this.cooldown = 100;
        this.active = false;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().lookAt(this.closestPlayer, this.mob.getBodyYawSpeed() + 20, this.mob.getLookPitchSpeed());
        if (this.mob.squaredDistanceTo(this.closestPlayer) < 6.25) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().startMovingTo(this.closestPlayer, this.speed);
        }
    }

    public boolean isActive() {
        return this.active;
    }
}

