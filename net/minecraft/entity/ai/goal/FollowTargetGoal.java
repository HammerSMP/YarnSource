/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

public class FollowTargetGoal<T extends LivingEntity>
extends TrackTargetGoal {
    protected final Class<T> targetClass;
    protected final int reciprocalChance;
    protected LivingEntity targetEntity;
    protected TargetPredicate targetPredicate;

    public FollowTargetGoal(MobEntity arg, Class<T> class_, boolean bl) {
        this(arg, class_, bl, false);
    }

    public FollowTargetGoal(MobEntity arg, Class<T> class_, boolean bl, boolean bl2) {
        this(arg, class_, 10, bl, bl2, null);
    }

    public FollowTargetGoal(MobEntity arg, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
        super(arg, bl, bl2);
        this.targetClass = class_;
        this.reciprocalChance = i;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
        this.targetPredicate = new TargetPredicate().setBaseMaxDistance(this.getFollowRange()).setPredicate(predicate);
    }

    @Override
    public boolean canStart() {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        }
        this.findClosestTarget();
        return this.targetEntity != null;
    }

    protected Box getSearchBox(double d) {
        return this.mob.getBoundingBox().expand(d, 4.0, d);
    }

    protected void findClosestTarget() {
        this.targetEntity = this.targetClass == PlayerEntity.class || this.targetClass == ServerPlayerEntity.class ? this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : this.mob.world.getClosestEntityIncludingUngeneratedChunks(this.targetClass, this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getSearchBox(this.getFollowRange()));
    }

    @Override
    public void start() {
        this.mob.setTarget(this.targetEntity);
        super.start();
    }

    public void setTargetEntity(@Nullable LivingEntity arg) {
        this.targetEntity = arg;
    }
}

