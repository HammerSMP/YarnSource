/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class AdmireItemTask<E extends PiglinEntity>
extends Task<E> {
    private final int duration;

    public AdmireItemTask(int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ADMIRING_ITEM, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.ADMIRING_DISABLED, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.duration = i;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        ItemEntity lv = ((PiglinEntity)arg2).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
        return PiglinBrain.isGoldenItem(lv.getStack().getItem());
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        ((PiglinEntity)arg2).getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, this.duration);
    }
}

