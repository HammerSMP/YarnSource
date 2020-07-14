/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class HideInHomeTask
extends Task<LivingEntity> {
    private final float walkSpeed;
    private final int maxDistance;
    private final int preferredDistance;
    private Optional<BlockPos> homePosition = Optional.empty();

    public HideInHomeTask(int maxDistance, float walkSpeed, int preferredDistance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.HIDING_PLACE, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.maxDistance = maxDistance;
        this.walkSpeed = walkSpeed;
        this.preferredDistance = preferredDistance;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        Optional<BlockPos> optional = world.getPointOfInterestStorage().getPosition(arg -> arg == PointOfInterestType.HOME, arg -> true, entity.getBlockPos(), this.preferredDistance + 1, PointOfInterestStorage.OccupationStatus.ANY);
        this.homePosition = optional.isPresent() && optional.get().isWithinDistance(entity.getPos(), (double)this.preferredDistance) ? optional : Optional.empty();
        return true;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Optional<GlobalPos> optional2;
        Brain<?> lv = entity.getBrain();
        Optional<BlockPos> optional = this.homePosition;
        if (!optional.isPresent() && !(optional = world.getPointOfInterestStorage().getPosition(arg -> arg == PointOfInterestType.HOME, arg -> true, PointOfInterestStorage.OccupationStatus.ANY, entity.getBlockPos(), this.maxDistance, entity.getRandom())).isPresent() && (optional2 = lv.getOptionalMemory(MemoryModuleType.HOME)).isPresent()) {
            optional = Optional.of(optional2.get().getPos());
        }
        if (optional.isPresent()) {
            lv.forget(MemoryModuleType.PATH);
            lv.forget(MemoryModuleType.LOOK_TARGET);
            lv.forget(MemoryModuleType.BREED_TARGET);
            lv.forget(MemoryModuleType.INTERACTION_TARGET);
            lv.remember(MemoryModuleType.HIDING_PLACE, GlobalPos.create(world.getRegistryKey(), optional.get()));
            if (!optional.get().isWithinDistance(entity.getPos(), (double)this.preferredDistance)) {
                lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(optional.get(), this.walkSpeed, this.preferredDistance));
            }
        }
    }
}

