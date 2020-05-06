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

    public FindEntityTask(EntityType<? extends T> arg, int i, Predicate<E> predicate, Predicate<T> predicate2, MemoryModuleType<T> arg2, float f, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityType = arg;
        this.speed = f;
        this.maxSquaredDistance = i * i;
        this.completionRange = j;
        this.predicate = predicate2;
        this.shouldRunPredicate = predicate;
        this.targetModule = arg2;
    }

    public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(EntityType<? extends T> arg2, int i, MemoryModuleType<T> arg22, float f, int j) {
        return new FindEntityTask<LivingEntity, LivingEntity>(arg2, i, arg -> true, arg -> true, arg22, f, j);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return this.shouldRunPredicate.test(arg2) && this.method_24582(arg2);
    }

    private boolean method_24582(E arg) {
        List<LivingEntity> list = ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
        return list.stream().anyMatch(this::method_24583);
    }

    private boolean method_24583(LivingEntity arg) {
        return this.entityType.equals(arg.getType()) && this.predicate.test(arg);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        Brain<?> lv = ((LivingEntity)arg2).getBrain();
        lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(arg -> this.entityType.equals(arg.getType())).map(arg -> arg).filter(arg2 -> arg2.squaredDistanceTo((Entity)arg2) <= (double)this.maxSquaredDistance).filter(this.predicate).findFirst().ifPresent(arg2 -> {
            lv.remember(this.targetModule, arg2);
            lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)arg2, true));
            lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget((Entity)arg2, false), this.speed, this.completionRange));
        }));
    }
}

