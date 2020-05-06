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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class LoseJobOnSiteLossTask
extends Task<VillagerEntity> {
    public LoseJobOnSiteLossTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        VillagerData lv = arg2.getVillagerData();
        return lv.getProfession() != VillagerProfession.NONE && lv.getProfession() != VillagerProfession.NITWIT && arg2.getExperience() == 0 && lv.getLevel() <= 1;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        arg2.setVillagerData(arg2.getVillagerData().withProfession(VillagerProfession.NONE));
        arg2.reinitializeBrain(arg);
    }
}

