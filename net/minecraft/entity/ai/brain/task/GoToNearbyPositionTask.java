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
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class GoToNearbyPositionTask
extends Task<PathAwareEntity> {
    private final MemoryModuleType<GlobalPos> memoryModuleType;
    private final int completionRange;
    private final int maxDistance;
    private final float field_25753;
    private long nextRunTime;

    public GoToNearbyPositionTask(MemoryModuleType<GlobalPos> memoryModuleType, float f, int i, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), memoryModuleType, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.memoryModuleType = memoryModuleType;
        this.field_25753 = f;
        this.completionRange = i;
        this.maxDistance = j;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, PathAwareEntity arg2) {
        Optional<GlobalPos> optional = arg2.getBrain().getOptionalMemory(this.memoryModuleType);
        return optional.isPresent() && arg.getRegistryKey() == optional.get().getDimension() && optional.get().getPos().isWithinDistance(arg2.getPos(), (double)this.maxDistance);
    }

    @Override
    protected void run(ServerWorld arg, PathAwareEntity arg22, long l) {
        if (l > this.nextRunTime) {
            Brain<?> lv = arg22.getBrain();
            Optional<GlobalPos> optional = lv.getOptionalMemory(this.memoryModuleType);
            optional.ifPresent(arg2 -> lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(arg2.getPos(), this.field_25753, this.completionRange)));
            this.nextRunTime = l + 80L;
        }
    }
}

