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

    public FollowMobTask(SpawnGroup arg, float f) {
        this((LivingEntity arg2) -> arg.equals((Object)arg2.getType().getSpawnGroup()), f);
    }

    public FollowMobTask(EntityType<?> arg, float f) {
        this((LivingEntity arg2) -> arg.equals(arg2.getType()), f);
    }

    public FollowMobTask(float f) {
        this((LivingEntity arg) -> true, f);
    }

    public FollowMobTask(Predicate<LivingEntity> predicate, float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.predicate = predicate;
        this.maxDistanceSquared = f * f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        return arg2.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(this.predicate);
    }

    @Override
    protected void run(ServerWorld arg, LivingEntity arg2, long l) {
        Brain<?> lv = arg2.getBrain();
        lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(this.predicate).filter(arg2 -> arg2.squaredDistanceTo(arg2) <= (double)this.maxDistanceSquared).findFirst().ifPresent(arg2 -> lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)arg2, true))));
    }
}

