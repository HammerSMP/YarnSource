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
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class WantNewItemTask<E extends PiglinEntity>
extends Task<E> {
    private final int range;

    public WantNewItemTask(int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.range = i;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        if (!((LivingEntity)arg2).getOffHandStack().isEmpty()) {
            return false;
        }
        Optional<ItemEntity> optional = ((PiglinEntity)arg2).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
        if (!optional.isPresent()) {
            return true;
        }
        return !optional.get().isInRange((Entity)arg2, this.range);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        ((PiglinEntity)arg2).getBrain().forget(MemoryModuleType.ADMIRING_ITEM);
    }
}

