/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class WalkToNearestVisibleWantedItemTask<E extends LivingEntity>
extends Task<E> {
    private final Predicate<E> startCondition;
    private final int radius;
    private final float field_23131;

    public WalkToNearestVisibleWantedItemTask(float f, boolean bl, int i) {
        this(arg -> true, f, bl, i);
    }

    public WalkToNearestVisibleWantedItemTask(Predicate<E> predicate, float f, boolean bl, int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)(bl ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT)), MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.startCondition = predicate;
        this.radius = i;
        this.field_23131 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return this.startCondition.test(arg2) && this.getNearestVisibleWantedItem(arg2).isInRange((Entity)arg2, this.radius);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        LookTargetUtil.walkTowards(arg2, this.getNearestVisibleWantedItem(arg2), this.field_23131, 0);
    }

    private ItemEntity getNearestVisibleWantedItem(E arg) {
        return ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}

