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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class ForgetAngryAtTargetTask<E extends MobEntity>
extends Task<E> {
    public ForgetAngryAtTargetTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        if (this.hasAngryAtTarget(arg2)) {
            ((LivingEntity)arg2).getBrain().forget(MemoryModuleType.ANGRY_AT);
        }
    }

    private boolean hasAngryAtTarget(E arg) {
        Optional<LivingEntity> optional = LookTargetUtil.getEntity(arg, MemoryModuleType.ANGRY_AT);
        return !optional.isPresent() || !optional.get().isAlive();
    }
}

