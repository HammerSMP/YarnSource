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
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class PanicTask
extends Task<VillagerEntity> {
    public PanicTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of());
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return PanicTask.wasHurt(arg2) || PanicTask.isHostileNearby(arg2);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        if (PanicTask.wasHurt(arg2) || PanicTask.isHostileNearby(arg2)) {
            Brain<VillagerEntity> lv = arg2.getBrain();
            if (!lv.hasActivity(Activity.PANIC)) {
                lv.forget(MemoryModuleType.PATH);
                lv.forget(MemoryModuleType.WALK_TARGET);
                lv.forget(MemoryModuleType.LOOK_TARGET);
                lv.forget(MemoryModuleType.BREED_TARGET);
                lv.forget(MemoryModuleType.INTERACTION_TARGET);
            }
            lv.doExclusively(Activity.PANIC);
        }
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        if (l % 100L == 0L) {
            arg2.summonGolem(arg, l, 3);
        }
    }

    public static boolean isHostileNearby(LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean wasHurt(LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}

