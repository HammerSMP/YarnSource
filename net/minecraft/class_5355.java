/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.IntRange;

public class class_5355<E extends PassiveEntity>
extends Task<E> {
    private final IntRange field_25357;
    private final float field_25358;

    public class_5355(IntRange arg, float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.field_25357 = arg;
        this.field_25358 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        if (!((PassiveEntity)arg2).isBaby()) {
            return false;
        }
        PassiveEntity lv = this.method_29520(arg2);
        return ((Entity)arg2).isInRange(lv, this.field_25357.method_29493() + 1) && !((Entity)arg2).isInRange(lv, this.field_25357.method_29492());
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        LookTargetUtil.walkTowards(arg2, this.method_29520(arg2), this.field_25358, this.field_25357.method_29492() - 1);
    }

    private PassiveEntity method_29520(E arg) {
        return ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}

