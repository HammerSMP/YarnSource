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
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;

public class StayAboveWaterTask
extends Task<MobEntity> {
    private final float chance;

    public StayAboveWaterTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of());
        this.chance = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntity arg2) {
        return arg2.isTouchingWater() && arg2.getFluidHeight(FluidTags.WATER) > arg2.method_29241() || arg2.isInLava();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, MobEntity arg2, long l) {
        return this.shouldRun(arg, arg2);
    }

    @Override
    protected void keepRunning(ServerWorld arg, MobEntity arg2, long l) {
        if (arg2.getRandom().nextFloat() < this.chance) {
            arg2.getJumpControl().setActive();
        }
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (MobEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (MobEntity)arg2, l);
    }
}

