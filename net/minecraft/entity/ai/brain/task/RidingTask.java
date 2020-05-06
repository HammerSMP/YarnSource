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

    public RidingTask(int i, BiPredicate<E, Entity> biPredicate) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.RIDE_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.range = i;
        this.alternativeRideCondition = biPredicate;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        Entity lv = ((Entity)arg2).getVehicle();
        Entity lv2 = ((LivingEntity)arg2).getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).orElse(null);
        if (lv == null && lv2 == null) {
            return false;
        }
        Entity lv3 = lv == null ? lv2 : lv;
        return !this.canRideTarget(arg2, lv3) || this.alternativeRideCondition.test(arg2, lv3);
    }

    private boolean canRideTarget(E arg, Entity arg2) {
        return arg2.isAlive() && arg2.isInRange((Entity)arg, this.range) && arg2.world == ((LivingEntity)arg).world;
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        ((LivingEntity)arg2).stopRiding();
        ((LivingEntity)arg2).getBrain().forget(MemoryModuleType.RIDE_TARGET);
    }
}

