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
import net.minecraft.world.poi.PointOfInterestStorage;

public class WalkTowardJobSiteTask
extends Task<VillagerEntity> {
    final float speed;

    public WalkTowardJobSiteTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), 1200);
        this.speed = f;
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
        LookTargetUtil.walkTowards((LivingEntity)arg2, arg2.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos(), this.speed, 1);
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        Optional<GlobalPos> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        optional.ifPresent(arg22 -> {
            BlockPos lv = arg22.getPos();
            ServerWorld lv2 = arg.getServer().getWorld(arg22.getDimension());
            if (lv2 == null) {
                return;
            }
            PointOfInterestStorage lv3 = lv2.getPointOfInterestStorage();
            if (lv3.test(lv, arg -> true)) {
                lv3.releaseTicket(lv);
            }
            DebugInfoSender.sendPointOfInterest(arg, lv);
        });
        arg2.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
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

