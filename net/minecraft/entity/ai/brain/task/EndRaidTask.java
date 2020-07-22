/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;

public class EndRaidTask
extends Task<LivingEntity> {
    public EndRaidTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return world.random.nextInt(20) == 0;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Brain<?> lv = entity.getBrain();
        Raid lv2 = world.getRaidAt(entity.getBlockPos());
        if (lv2 == null || lv2.hasStopped() || lv2.hasLost()) {
            lv.setDefaultActivity(Activity.IDLE);
            lv.refreshActivities(world.getTimeOfDay(), world.getTime());
        }
    }
}

