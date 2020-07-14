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
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class StrollTask
extends Task<PathAwareEntity> {
    private final float speed;
    private final int horizontalRadius;
    private final int verticalRadius;

    public StrollTask(float speed) {
        this(speed, 10, 7);
    }

    public StrollTask(float speed, int horizontalRadius, int verticalRadius) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = speed;
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
    }

    @Override
    protected void run(ServerWorld arg2, PathAwareEntity arg22, long l) {
        Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(arg22, this.horizontalRadius, this.verticalRadius));
        arg22.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(arg -> new WalkTarget((Vec3d)arg, this.speed, 0)));
    }
}

