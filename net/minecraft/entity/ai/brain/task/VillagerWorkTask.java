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
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.dynamic.Timestamp;

public class VillagerWorkTask
extends Task<VillagerEntity> {
    private long lastCheckedTime;

    public VillagerWorkTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        if (arg.getTime() - this.lastCheckedTime < 300L) {
            return false;
        }
        if (arg.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheckedTime = arg.getTime();
        GlobalPos lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        return lv.getDimension() == arg.method_27983() && lv.getPos().isWithinDistance(arg2.getPos(), 1.73);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg22, long l) {
        Brain<VillagerEntity> lv = arg22.getBrain();
        lv.remember(MemoryModuleType.LAST_WORKED_AT_POI, Timestamp.of(l));
        lv.getOptionalMemory(MemoryModuleType.JOB_SITE).ifPresent(arg2 -> lv.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(arg2.getPos())));
        arg22.playWorkSound();
        this.performAdditionalWork(arg, arg22);
        if (arg22.shouldRestock()) {
            arg22.restock();
        }
    }

    protected void performAdditionalWork(ServerWorld arg, VillagerEntity arg2) {
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        Optional<GlobalPos> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        if (!optional.isPresent()) {
            return false;
        }
        GlobalPos lv = optional.get();
        return lv.getDimension() == arg.method_27983() && lv.getPos().isWithinDistance(arg2.getPos(), 1.73);
    }

    @Override
    protected /* synthetic */ boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        return this.shouldRun(arg, (VillagerEntity)arg2);
    }
}

