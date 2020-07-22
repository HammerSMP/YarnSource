/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class TakeJobSiteTask
extends Task<VillagerEntity> {
    private final float speed;

    public TakeJobSiteTask(float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        if (arg2.isBaby()) {
            return false;
        }
        return arg2.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg22, long l) {
        BlockPos lv = arg22.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        Optional<PointOfInterestType> optional = arg.getPointOfInterestStorage().getType(lv);
        if (!optional.isPresent()) {
            return;
        }
        LookTargetUtil.streamSeenVillagers(arg22, arg2 -> this.canUseJobSite((PointOfInterestType)optional.get(), (VillagerEntity)arg2, lv)).findFirst().ifPresent(arg4 -> this.claimSite(arg, arg22, (VillagerEntity)arg4, lv, arg4.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent()));
    }

    private boolean canUseJobSite(PointOfInterestType poiType, VillagerEntity villager, BlockPos pos) {
        boolean bl = villager.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
        if (bl) {
            return false;
        }
        Optional<GlobalPos> optional = villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        VillagerProfession lv = villager.getVillagerData().getProfession();
        if (villager.getVillagerData().getProfession() != VillagerProfession.NONE && lv.getWorkStation().getCompletionCondition().test(poiType)) {
            if (!optional.isPresent()) {
                return this.canReachJobSite(villager, pos, poiType);
            }
            return optional.get().getPos().equals(pos);
        }
        return false;
    }

    private void claimSite(ServerWorld world, VillagerEntity previousOwner, VillagerEntity newOwner, BlockPos pos, boolean jobSitePresent) {
        this.forgetJobSiteAndWalkTarget(previousOwner);
        if (!jobSitePresent) {
            LookTargetUtil.walkTowards((LivingEntity)newOwner, pos, this.speed, 1);
            newOwner.getBrain().remember(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.create(world.getRegistryKey(), pos));
            DebugInfoSender.sendPointOfInterest(world, pos);
        }
    }

    private boolean canReachJobSite(VillagerEntity villager, BlockPos pos, PointOfInterestType poiType) {
        Path lv = villager.getNavigation().findPathTo(pos, poiType.getSearchDistance());
        return lv != null && lv.reachesTarget();
    }

    private void forgetJobSiteAndWalkTarget(VillagerEntity villager) {
        villager.getBrain().forget(MemoryModuleType.WALK_TARGET);
        villager.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villager.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

