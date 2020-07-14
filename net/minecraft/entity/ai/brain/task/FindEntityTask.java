/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class FindEntityTask<E extends LivingEntity, T extends LivingEntity>
extends Task<E> {
    private final int completionRange;
    private final float speed;
    private final EntityType<? extends T> entityType;
    private final int maxSquaredDistance;
    private final Predicate<T> predicate;
    private final Predicate<E> shouldRunPredicate;
    private final MemoryModuleType<T> targetModule;

    public FindEntityTask(EntityType<? extends T> entityType, int maxDistance, Predicate<E> shouldRunPredicate, Predicate<T> predicate, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityType = entityType;
        this.speed = speed;
        this.maxSquaredDistance = maxDistance * maxDistance;
        this.completionRange = completionRange;
        this.predicate = predicate;
        this.shouldRunPredicate = shouldRunPredicate;
        this.targetModule = targetModule;
    }

    public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(EntityType<? extends T> entityType, int maxDistance, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        return new FindEntityTask<LivingEntity, LivingEntity>(entityType, maxDistance, arg -> true, arg -> true, targetModule, speed, completionRange);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return this.shouldRunPredicate.test(entity) && this.method_24582(entity);
    }

    private boolean method_24582(E arg) {
        List<LivingEntity> list = ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
        return list.stream().anyMatch(this::method_24583);
    }

    private boolean method_24583(LivingEntity arg) {
        return this.entityType.equals(arg.getType()) && this.predicate.test(arg);
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        Brain<?> lv = ((LivingEntity)entity).getBrain();
        lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(arg -> this.entityType.equals(arg.getType())).map(arg -> arg).filter(arg2 -> arg2.squaredDistanceTo((Entity)entity) <= (double)this.maxSquaredDistance).filter(this.predicate).findFirst().ifPresent(arg2 -> {
            lv.remember(this.targetModule, arg2);
            lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)arg2, true));
            lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget((Entity)arg2, false), this.speed, this.completionRange));
        }));
    }
}

