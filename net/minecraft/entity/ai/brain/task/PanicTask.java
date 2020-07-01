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
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long worldTime) {
        if (worldTime % 100L == 0L) {
            arg2.summonGolem(arg, worldTime, 3);
        }
    }

    public static boolean isHostileNearby(LivingEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean wasHurt(LivingEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld arg, LivingEntity arg2, long l) {
        this.run(arg, (VillagerEntity)arg2, l);
    }
}

