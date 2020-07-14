/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.IntRange;

public class WalkTowardClosestAdultTask<E extends PassiveEntity>
extends Task<E> {
    private final IntRange executionRange;
    private final float speed;

    public WalkTowardClosestAdultTask(IntRange executionRange, float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.executionRange = executionRange;
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        if (!((PassiveEntity)arg2).isBaby()) {
            return false;
        }
        PassiveEntity lv = this.getNearestVisibleAdult(arg2);
        return ((Entity)arg2).isInRange(lv, this.executionRange.getMax() + 1) && !((Entity)arg2).isInRange(lv, this.executionRange.getMin());
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        LookTargetUtil.walkTowards(arg2, this.getNearestVisibleAdult(arg2), this.speed, this.executionRange.getMin() - 1);
    }

    private PassiveEntity getNearestVisibleAdult(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}

