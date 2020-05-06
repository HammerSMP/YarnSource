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
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class ForgetAttackTargetTask<E extends MobEntity>
extends Task<E> {
    private final Predicate<LivingEntity> alternativeCondition;

    public ForgetAttackTargetTask(Predicate<LivingEntity> predicate) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.alternativeCondition = predicate;
    }

    public ForgetAttackTargetTask() {
        this((LivingEntity arg) -> false);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        if (ForgetAttackTargetTask.cannotReachTarget(arg2)) {
            this.forgetAttackTarget(arg2);
            return;
        }
        if (this.isAttackTargetDead(arg2)) {
            this.forgetAttackTarget(arg2);
            return;
        }
        if (this.isAttackTargetInAnotherWorld(arg2)) {
            this.forgetAttackTarget(arg2);
            return;
        }
        if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(this.getAttackTarget(arg2))) {
            this.forgetAttackTarget(arg2);
            return;
        }
        if (this.alternativeCondition.test(this.getAttackTarget(arg2))) {
            this.forgetAttackTarget(arg2);
            return;
        }
    }

    private boolean isAttackTargetInAnotherWorld(E arg) {
        return this.getAttackTarget(arg).world != ((MobEntity)arg).world;
    }

    private LivingEntity getAttackTarget(E arg) {
        return ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends LivingEntity> boolean cannotReachTarget(E arg) {
        Optional<Long> optional = arg.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return optional.isPresent() && arg.world.getTime() - optional.get() > 200L;
    }

    private boolean isAttackTargetDead(E arg) {
        Optional<LivingEntity> optional = ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        return optional.isPresent() && !optional.get().isAlive();
    }

    private void forgetAttackTarget(E arg) {
        ((LivingEntity)arg).getBrain().forget(MemoryModuleType.ATTACK_TARGET);
    }
}

