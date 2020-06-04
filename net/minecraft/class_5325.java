/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class class_5325
extends Task<VillagerEntity> {
    final float field_25155;

    public class_5325(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), 1200);
        this.field_25155 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg2, VillagerEntity arg22) {
        return arg22.getBrain().getFirstPossibleNonCoreActivity().map(arg -> arg == Activity.IDLE || arg == Activity.WORK || arg == Activity.PLAY).orElse(true);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return arg2.getBrain().hasMemoryModule(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        LookTargetUtil.walkTowards((LivingEntity)arg2, arg2.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos(), this.field_25155, 1);
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg22, long l) {
        Optional<GlobalPos> optional = arg22.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        optional.ifPresent(arg2 -> {
            BlockPos lv = arg2.getPos();
            arg.getPointOfInterestStorage().releaseTicket(lv);
            DebugInfoSender.sendPointOfInterest(arg, lv);
        });
        arg22.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (VillagerEntity)arg2, l);
    }
}

