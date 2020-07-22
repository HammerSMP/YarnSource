/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class TrackIronGolemTargetGoal
extends TrackTargetGoal {
    private final IronGolemEntity golem;
    private LivingEntity target;
    private final TargetPredicate targetPredicate = new TargetPredicate().setBaseMaxDistance(64.0);

    public TrackIronGolemTargetGoal(IronGolemEntity golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        Box lv = this.golem.getBoundingBox().expand(10.0, 8.0, 10.0);
        List<VillagerEntity> list = this.golem.world.getTargets(VillagerEntity.class, this.targetPredicate, this.golem, lv);
        List<PlayerEntity> list2 = this.golem.world.getPlayers(this.targetPredicate, this.golem, lv);
        for (LivingEntity livingEntity : list) {
            VillagerEntity lv3 = (VillagerEntity)livingEntity;
            for (PlayerEntity lv4 : list2) {
                int i = lv3.getReputation(lv4);
                if (i > -100) continue;
                this.target = lv4;
            }
        }
        if (this.target == null) {
            return false;
        }
        return !(this.target instanceof PlayerEntity) || !this.target.isSpectator() && !((PlayerEntity)this.target).isCreative();
    }

    @Override
    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}

