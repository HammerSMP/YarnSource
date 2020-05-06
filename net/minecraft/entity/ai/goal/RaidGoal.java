/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.raid.RaiderEntity;

public class RaidGoal<T extends LivingEntity>
extends FollowTargetGoal<T> {
    private int cooldown = 0;

    public RaidGoal(RaiderEntity arg, Class<T> arg2, boolean bl, @Nullable Predicate<LivingEntity> predicate) {
        super(arg, arg2, 500, bl, false, predicate);
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void decreaseCooldown() {
        --this.cooldown;
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0 || !this.mob.getRandom().nextBoolean()) {
            return false;
        }
        if (!((RaiderEntity)this.mob).hasActiveRaid()) {
            return false;
        }
        this.findClosestTarget();
        return this.targetEntity != null;
    }

    @Override
    public void start() {
        this.cooldown = 200;
        super.start();
    }
}

