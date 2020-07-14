/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class GoToSecondaryPositionTask
extends Task<VillagerEntity> {
    private final MemoryModuleType<List<GlobalPos>> secondaryPositions;
    private final MemoryModuleType<GlobalPos> primaryPosition;
    private final float speed;
    private final int completionRange;
    private final int primaryPositionActivationDistance;
    private long nextRunTime;
    @Nullable
    private GlobalPos chosenPosition;

    public GoToSecondaryPositionTask(MemoryModuleType<List<GlobalPos>> secondaryPositions, float speed, int completionRange, int primaryPositionActivationDistance, MemoryModuleType<GlobalPos> primaryPosition) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), secondaryPositions, (Object)((Object)MemoryModuleState.VALUE_PRESENT), primaryPosition, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.secondaryPositions = secondaryPositions;
        this.speed = speed;
        this.completionRange = completionRange;
        this.primaryPositionActivationDistance = primaryPositionActivationDistance;
        this.primaryPosition = primaryPosition;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        List<GlobalPos> list;
        Optional<List<GlobalPos>> optional = arg2.getBrain().getOptionalMemory(this.secondaryPositions);
        Optional<GlobalPos> optional2 = arg2.getBrain().getOptionalMemory(this.primaryPosition);
        if (optional.isPresent() && optional2.isPresent() && !(list = optional.get()).isEmpty()) {
            this.chosenPosition = list.get(arg.getRandom().nextInt(list.size()));
            return this.chosenPosition != null && arg.getRegistryKey() == this.chosenPosition.getDimension() && optional2.get().getPos().isWithinDistance(arg2.getPos(), (double)this.primaryPositionActivationDistance);
        }
        return false;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        if (l > this.nextRunTime && this.chosenPosition != null) {
            arg2.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(this.chosenPosition.getPos(), this.speed, this.completionRange));
            this.nextRunTime = l + 100L;
        }
    }
}

