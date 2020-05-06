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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class FindInteractionTargetTask
extends Task<LivingEntity> {
    private final EntityType<?> entityType;
    private final int maxSquaredDistance;
    private final Predicate<LivingEntity> predicate;
    private final Predicate<LivingEntity> shouldRunPredicate;

    public FindInteractionTargetTask(EntityType<?> arg, int i, Predicate<LivingEntity> predicate, Predicate<LivingEntity> predicate2) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityType = arg;
        this.maxSquaredDistance = i * i;
        this.predicate = predicate2;
        this.shouldRunPredicate = predicate;
    }

    public FindInteractionTargetTask(EntityType<?> arg2, int i) {
        this(arg2, i, arg -> true, arg -> true);
    }

    @Override
    public boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        return this.shouldRunPredicate.test(arg2) && this.getVisibleMobs(arg2).stream().anyMatch(this::test);
    }

    @Override
    public void run(ServerWorld arg, LivingEntity arg2, long l) {
        super.run(arg, arg2, l);
        Brain<?> lv = arg2.getBrain();
        lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(arg2 -> arg2.squaredDistanceTo(arg2) <= (double)this.maxSquaredDistance).filter(this::test).findFirst().ifPresent(arg2 -> {
            lv.remember(MemoryModuleType.INTERACTION_TARGET, arg2);
            lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)arg2, true));
        }));
    }

    private boolean test(LivingEntity arg) {
        return this.entityType.equals(arg.getType()) && this.predicate.test(arg);
    }

    private List<LivingEntity> getVisibleMobs(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
    }
}

