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
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class AttackTask<E extends MobEntity>
extends Task<E> {
    private final int distance;
    private final float forwardMovement;

    public AttackTask(int i, float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.distance = i;
        this.forwardMovement = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return this.isAttackTargetVisible(arg2) && this.isNearAttackTarget(arg2);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        ((LivingEntity)arg2).getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.getAttackTarget(arg2), true));
        ((MobEntity)arg2).getMoveControl().strafeTo(-this.forwardMovement, 0.0f);
        ((MobEntity)arg2).yaw = MathHelper.capRotation(((MobEntity)arg2).yaw, ((MobEntity)arg2).headYaw, 0.0f);
    }

    private boolean isAttackTargetVisible(E arg) {
        return ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().contains(this.getAttackTarget(arg));
    }

    private boolean isNearAttackTarget(E arg) {
        return this.getAttackTarget(arg).isInRange((Entity)arg, this.distance);
    }

    private LivingEntity getAttackTarget(E arg) {
        return ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

