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

    public HideInHomeTask(int i, float f, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.HIDING_PLACE, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.maxDistance = i;
        this.walkSpeed = f;
        this.preferredDistance = j;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg2, LivingEntity arg22) {
        Optional<BlockPos> optional = arg2.getPointOfInterestStorage().getPosition(arg -> arg == PointOfInterestType.HOME, arg -> true, arg22.getBlockPos(), this.preferredDistance + 1, PointOfInterestStorage.OccupationStatus.ANY);
        this.homePosition = optional.isPresent() && optional.get().isWithinDistance(arg22.getPos(), (double)this.preferredDistance) ? optional : Optional.empty();
        return true;
    }

    @Override
    protected void run(ServerWorld arg2, LivingEntity arg22, long l) {
        Optional<GlobalPos> optional2;
        Brain<?> lv = arg22.getBrain();
        Optional<BlockPos> optional = this.homePosition;
        if (!optional.isPresent() && !(optional = arg2.getPointOfInterestStorage().getPosition(arg -> arg == PointOfInterestType.HOME, arg -> true, PointOfInterestStorage.OccupationStatus.ANY, arg22.getBlockPos(), this.maxDistance, arg22.getRandom())).isPresent() && (optional2 = lv.getOptionalMemory(MemoryModuleType.HOME)).isPresent()) {
            optional = Optional.of(optional2.get().getPos());
        }
        if (optional.isPresent()) {
            lv.forget(MemoryModuleType.PATH);
            lv.forget(MemoryModuleType.LOOK_TARGET);
            lv.forget(MemoryModuleType.BREED_TARGET);
            lv.forget(MemoryModuleType.INTERACTION_TARGET);
            lv.remember(MemoryModuleType.HIDING_PLACE, GlobalPos.create(arg2.method_27983(), optional.get()));
            if (!optional.get().isWithinDistance(arg22.getPos(), (double)this.preferredDistance)) {
                lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(optional.get(), this.walkSpeed, this.preferredDistance));
            }
        }
    }
}

