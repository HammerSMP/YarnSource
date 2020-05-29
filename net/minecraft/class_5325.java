/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class class_5325
extends Task<VillagerEntity> {
    final float field_25155;

    public class_5325(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.field_25155 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg2, VillagerEntity arg22) {
        return arg22.getBrain().getFirstPossibleNonCoreActivity().map(arg -> arg == Activity.IDLE || arg == Activity.WORK || arg == Activity.PLAY).orElse(true);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        LookTargetUtil.walkTowards((LivingEntity)arg2, arg2.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos(), this.field_25155, 1);
    }
}

