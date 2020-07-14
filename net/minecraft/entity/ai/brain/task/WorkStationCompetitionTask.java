/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class WorkStationCompetitionTask
extends Task<VillagerEntity> {
    final VillagerProfession profession;

    public WorkStationCompetitionTask(VillagerProfession profession) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.profession = profession;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        GlobalPos lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        arg.getPointOfInterestStorage().getType(lv.getPos()).ifPresent(arg32 -> LookTargetUtil.streamSeenVillagers(arg2, arg3 -> this.isUsingWorkStationAt(lv, (PointOfInterestType)arg32, (VillagerEntity)arg3)).reduce(arg2, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager));
    }

    private static VillagerEntity keepJobSiteForMoreExperiencedVillager(VillagerEntity first, VillagerEntity second) {
        VillagerEntity lv4;
        VillagerEntity lv3;
        if (first.getExperience() > second.getExperience()) {
            VillagerEntity lv = first;
            VillagerEntity lv2 = second;
        } else {
            lv3 = second;
            lv4 = first;
        }
        lv4.getBrain().forget(MemoryModuleType.JOB_SITE);
        return lv3;
    }

    private boolean isUsingWorkStationAt(GlobalPos pos, PointOfInterestType poiType, VillagerEntity villager) {
        return this.hasJobSite(villager) && pos.equals(villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get()) && this.isCompletedWorkStation(poiType, villager.getVillagerData().getProfession());
    }

    private boolean isCompletedWorkStation(PointOfInterestType poiType, VillagerProfession profession) {
        return profession.getWorkStation().getCompletionCondition().test(poiType);
    }

    private boolean hasJobSite(VillagerEntity villager) {
        return villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}

