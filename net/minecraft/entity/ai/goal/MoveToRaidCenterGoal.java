/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class MoveToRaidCenterGoal<T extends RaiderEntity>
extends Goal {
    private final T actor;

    public MoveToRaidCenterGoal(T arg) {
        this.actor = arg;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return ((MobEntity)this.actor).getTarget() == null && !((Entity)this.actor).hasPassengers() && ((RaiderEntity)this.actor).hasActiveRaid() && !((RaiderEntity)this.actor).getRaid().isFinished() && !((ServerWorld)((RaiderEntity)this.actor).world).isNearOccupiedPointOfInterest(((Entity)this.actor).getBlockPos());
    }

    @Override
    public boolean shouldContinue() {
        return ((RaiderEntity)this.actor).hasActiveRaid() && !((RaiderEntity)this.actor).getRaid().isFinished() && ((RaiderEntity)this.actor).world instanceof ServerWorld && !((ServerWorld)((RaiderEntity)this.actor).world).isNearOccupiedPointOfInterest(((Entity)this.actor).getBlockPos());
    }

    @Override
    public void tick() {
        if (((RaiderEntity)this.actor).hasActiveRaid()) {
            Vec3d lv2;
            Raid lv = ((RaiderEntity)this.actor).getRaid();
            if (((RaiderEntity)this.actor).age % 20 == 0) {
                this.includeFreeRaiders(lv);
            }
            if (!((MobEntityWithAi)this.actor).isNavigating() && (lv2 = TargetFinder.findTargetTowards(this.actor, 15, 4, Vec3d.method_24955(lv.getCenter()))) != null) {
                ((MobEntity)this.actor).getNavigation().startMovingTo(lv2.x, lv2.y, lv2.z, 1.0);
            }
        }
    }

    private void includeFreeRaiders(Raid arg) {
        if (arg.isActive()) {
            HashSet set = Sets.newHashSet();
            List<RaiderEntity> list = ((RaiderEntity)this.actor).world.getEntities(RaiderEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0), arg2 -> !arg2.hasActiveRaid() && RaidManager.isValidRaiderFor(arg2, arg));
            set.addAll(list);
            for (RaiderEntity lv : set) {
                arg.addRaider(arg.getGroupsSpawned(), lv, null, true);
            }
        }
    }
}

