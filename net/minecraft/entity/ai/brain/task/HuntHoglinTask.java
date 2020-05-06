/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class HuntHoglinTask<E extends PiglinEntity>
extends Task<E> {
    public HuntHoglinTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.HUNTED_RECENTLY, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, PiglinEntity arg2) {
        return !arg2.isBaby() && !PiglinBrain.haveHuntedHoglinsRecently(arg2);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        HoglinEntity lv = ((PiglinEntity)arg2).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
        PiglinBrain.angerAt(arg2, lv);
        PiglinBrain.rememberHunting(arg2);
        PiglinBrain.angerAtCloserTargets(arg2, lv);
        PiglinBrain.rememberGroupHunting(arg2);
    }
}

