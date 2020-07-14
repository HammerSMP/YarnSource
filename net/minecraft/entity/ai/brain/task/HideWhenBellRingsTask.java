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

public class HideWhenBellRingsTask
extends Task<LivingEntity> {
    public HideWhenBellRingsTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.HEARD_BELL_TIME, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Brain<?> lv = entity.getBrain();
        Raid lv2 = world.getRaidAt(entity.getBlockPos());
        if (lv2 == null) {
            lv.doExclusively(Activity.HIDE);
        }
    }
}

