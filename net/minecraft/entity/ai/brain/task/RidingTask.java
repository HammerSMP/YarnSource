/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class RidingTask<E extends LivingEntity, T extends Entity>
extends Task<E> {
    private final int range;
    private final BiPredicate<E, Entity> alternativeRideCondition;

    public RidingTask(int range, BiPredicate<E, Entity> alternativeRideCondition) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.RIDE_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.range = range;
        this.alternativeRideCondition = alternativeRideCondition;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        Entity lv = ((Entity)entity).getVehicle();
        Entity lv2 = ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).orElse(null);
        if (lv == null && lv2 == null) {
            return false;
        }
        Entity lv3 = lv == null ? lv2 : lv;
        return !this.canRideTarget(entity, lv3) || this.alternativeRideCondition.test(entity, lv3);
    }

    private boolean canRideTarget(E entity, Entity target) {
        return target.isAlive() && target.isInRange((Entity)entity, this.range) && target.world == ((LivingEntity)entity).world;
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        ((LivingEntity)entity).stopRiding();
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.RIDE_TARGET);
    }
}

