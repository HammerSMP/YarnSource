/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

public class LookAtEntityGoal
extends Goal {
    protected final MobEntity mob;
    protected Entity target;
    protected final float range;
    private int lookTime;
    protected final float chance;
    protected final Class<? extends LivingEntity> targetType;
    protected final TargetPredicate targetPredicate;

    public LookAtEntityGoal(MobEntity arg, Class<? extends LivingEntity> class_, float f) {
        this(arg, class_, f, 0.02f);
    }

    public LookAtEntityGoal(MobEntity arg, Class<? extends LivingEntity> class_, float f, float g) {
        this.mob = arg;
        this.targetType = class_;
        this.range = f;
        this.chance = g;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
        this.targetPredicate = class_ == PlayerEntity.class ? new TargetPredicate().setBaseMaxDistance(f).includeTeammates().includeInvulnerable().ignoreEntityTargetRules().setPredicate(arg2 -> EntityPredicates.rides(arg).test((Entity)arg2)) : new TargetPredicate().setBaseMaxDistance(f).includeTeammates().includeInvulnerable().ignoreEntityTargetRules();
    }

    @Override
    public boolean canStart() {
        if (this.mob.getRandom().nextFloat() >= this.chance) {
            return false;
        }
        if (this.mob.getTarget() != null) {
            this.target = this.mob.getTarget();
        }
        this.target = this.targetType == PlayerEntity.class ? this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : this.mob.world.getClosestEntityIncludingUngeneratedChunks(this.targetType, this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.mob.getBoundingBox().expand(this.range, 3.0, this.range));
        return this.target != null;
    }

    @Override
    public boolean shouldContinue() {
        if (!this.target.isAlive()) {
            return false;
        }
        if (this.mob.squaredDistanceTo(this.target) > (double)(this.range * this.range)) {
            return false;
        }
        return this.lookTime > 0;
    }

    @Override
    public void start() {
        this.lookTime = 40 + this.mob.getRandom().nextInt(40);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().lookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        --this.lookTime;
    }
}

