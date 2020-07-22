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

    public WalkToNearestVisibleWantedItemTask(Predicate<E> startCondition, float f, boolean requiresWalkTarget, int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)(requiresWalkTarget ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT)), MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.startCondition = startCondition;
        this.radius = i;
        this.field_23131 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return this.startCondition.test(entity) && this.getNearestVisibleWantedItem(entity).isInRange((Entity)entity, this.radius);
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        LookTargetUtil.walkTowards(entity, this.getNearestVisibleWantedItem(entity), this.field_23131, 0);
    }

    private ItemEntity getNearestVisibleWantedItem(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}

