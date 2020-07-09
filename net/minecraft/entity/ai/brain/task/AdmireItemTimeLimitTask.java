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
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class AdmireItemTimeLimitTask<E extends PiglinEntity>
extends Task<E> {
    private final int timeLimit;
    private final int cooldown;

    public AdmireItemTimeLimitTask(int i, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.timeLimit = i;
        this.cooldown = j;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return ((LivingEntity)arg2).getOffHandStack().isEmpty();
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        Brain<PiglinEntity> lv = ((PiglinEntity)arg2).getBrain();
        Optional<Integer> optional = lv.getOptionalMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
        if (!optional.isPresent()) {
            lv.remember(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, 0);
        } else {
            int i = optional.get();
            if (i > this.timeLimit) {
                lv.forget(MemoryModuleType.ADMIRING_ITEM);
                lv.forget(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
                lv.remember(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, this.cooldown);
            } else {
                lv.remember(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, i + 1);
            }
        }
    }
}

