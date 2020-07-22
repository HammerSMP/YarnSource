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
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class UpdateAttackTargetTask<E extends MobEntity>
extends Task<E> {
    private final Predicate<E> startCondition;
    private final Function<E, Optional<? extends LivingEntity>> targetGetter;

    public UpdateAttackTargetTask(Predicate<E> startCondition, Function<E, Optional<? extends LivingEntity>> targetGetter) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.startCondition = startCondition;
        this.targetGetter = targetGetter;
    }

    public UpdateAttackTargetTask(Function<E, Optional<? extends LivingEntity>> targetGetter) {
        this((E arg) -> true, targetGetter);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        if (!this.startCondition.test(arg2)) {
            return false;
        }
        Optional<? extends LivingEntity> optional = this.targetGetter.apply(arg2);
        return optional.isPresent() && optional.get().isAlive();
    }

    @Override
    protected void run(ServerWorld arg, E arg22, long l) {
        this.targetGetter.apply(arg22).ifPresent(arg2 -> this.updateAttackTarget(arg22, (LivingEntity)arg2));
    }

    private void updateAttackTarget(E entity, LivingEntity target) {
        ((LivingEntity)entity).getBrain().remember(MemoryModuleType.ATTACK_TARGET, target);
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}

