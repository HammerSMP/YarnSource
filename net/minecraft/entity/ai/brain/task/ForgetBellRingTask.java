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
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ForgetBellRingTask
extends Task<LivingEntity> {
    private final int distance;
    private final int maxHiddenTicks;
    private int hiddenTicks;

    public ForgetBellRingTask(int maxHiddenSeconds, int distance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.HIDING_PLACE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.HEARD_BELL_TIME, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.maxHiddenTicks = maxHiddenSeconds * 20;
        this.hiddenTicks = 0;
        this.distance = distance;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        boolean bl;
        Brain<?> lv = entity.getBrain();
        Optional<Long> optional = lv.getOptionalMemory(MemoryModuleType.HEARD_BELL_TIME);
        boolean bl2 = bl = optional.get() + 300L <= time;
        if (this.hiddenTicks > this.maxHiddenTicks || bl) {
            lv.forget(MemoryModuleType.HEARD_BELL_TIME);
            lv.forget(MemoryModuleType.HIDING_PLACE);
            lv.refreshActivities(world.getTimeOfDay(), world.getTime());
            this.hiddenTicks = 0;
            return;
        }
        BlockPos lv2 = lv.getOptionalMemory(MemoryModuleType.HIDING_PLACE).get().getPos();
        if (lv2.isWithinDistance(entity.getBlockPos(), (double)this.distance)) {
            ++this.hiddenTicks;
        }
    }
}

