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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class FollowMobTask
extends Task<LivingEntity> {
    private final Predicate<LivingEntity> predicate;
    private final float maxDistanceSquared;

    public FollowMobTask(SpawnGroup group, float maxDistance) {
        this((LivingEntity arg2) -> group.equals(arg2.getType().getSpawnGroup()), maxDistance);
    }

    public FollowMobTask(EntityType<?> type, float maxDistance) {
        this((LivingEntity arg2) -> type.equals(arg2.getType()), maxDistance);
    }

    public FollowMobTask(float maxDistance) {
        this((LivingEntity arg) -> true, maxDistance);
    }

    public FollowMobTask(Predicate<LivingEntity> predicate, float maxDistance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.predicate = predicate;
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(this.predicate);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Brain<?> lv = entity.getBrain();
        lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(this.predicate).filter(arg2 -> arg2.squaredDistanceTo(entity) <= (double)this.maxDistanceSquared).findFirst().ifPresent(arg2 -> lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)arg2, true))));
    }
}

