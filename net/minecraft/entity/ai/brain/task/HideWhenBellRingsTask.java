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
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;

public class HideWhenBellRingsTask
extends Task<LivingEntity> {
    public HideWhenBellRingsTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.HEARD_BELL_TIME, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected void run(ServerWorld arg, LivingEntity arg2, long l) {
        Brain<?> lv = arg2.getBrain();
        Raid lv2 = arg.getRaidAt(arg2.getBlockPos());
        if (lv2 == null) {
            lv.method_24526(Activity.HIDE);
        }
    }
}
