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

    public WorkStationCompetitionTask(VillagerProfession arg) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.profession = arg;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        GlobalPos lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        arg.getPointOfInterestStorage().getType(lv.getPos()).ifPresent(arg32 -> LookTargetUtil.streamSeenVillagers(arg2, arg3 -> this.isUsingWorkStationAt(lv, (PointOfInterestType)arg32, (VillagerEntity)arg3)).reduce(arg2, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager));
    }

    private static VillagerEntity keepJobSiteForMoreExperiencedVillager(VillagerEntity arg, VillagerEntity arg2) {
        VillagerEntity lv4;
        VillagerEntity lv3;
        if (arg.getExperience() > arg2.getExperience()) {
            VillagerEntity lv = arg;
            VillagerEntity lv2 = arg2;
        } else {
            lv3 = arg2;
            lv4 = arg;
        }
        lv4.getBrain().forget(MemoryModuleType.JOB_SITE);
        return lv3;
    }

    private boolean isUsingWorkStationAt(GlobalPos arg, PointOfInterestType arg2, VillagerEntity arg3) {
        return this.hasJobSite(arg3) && arg.equals(arg3.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get()) && this.isCompletedWorkStation(arg2, arg3.getVillagerData().getProfession());
    }

    private boolean isCompletedWorkStation(PointOfInterestType arg, VillagerProfession arg2) {
        return arg2.getWorkStation().getCompletionCondition().test(arg);
    }

    private boolean hasJobSite(VillagerEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}

