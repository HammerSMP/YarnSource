/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import net.minecraft.util.dynamic.GlobalPos;

public class MeetVillagerTask
extends Task<LivingEntity> {
    public MeetVillagerTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg2, LivingEntity arg22) {
        Brain<?> lv = arg22.getBrain();
        Optional<GlobalPos> optional = lv.getOptionalMemory(MemoryModuleType.MEETING_POINT);
        return arg2.getRandom().nextInt(100) == 0 && optional.isPresent() && Objects.equals(arg2.getDimension().getType(), optional.get().getDimension()) && optional.get().getPos().isWithinDistance(arg22.getPos(), 4.0) && lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(arg -> EntityType.VILLAGER.equals(arg.getType()));
    }

    @Override
    protected void run(ServerWorld arg, LivingEntity arg2, long l) {
        Brain<?> lv = arg2.getBrain();
        lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(arg -> EntityType.VILLAGER.equals(arg.getType())).filter(arg2 -> arg2.squaredDistanceTo(arg2) <= 32.0).findFirst().ifPresent(arg2 -> {
            lv.remember(MemoryModuleType.INTERACTION_TARGET, arg2);
            lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)arg2, true));
            lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget((Entity)arg2, false), 0.3f, 1));
        }));
    }
}

