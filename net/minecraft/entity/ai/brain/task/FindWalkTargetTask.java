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
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask
extends Task<PathAwareEntity> {
    private final float walkSpeed;
    private final int maxHorizontalDistance;
    private final int maxVerticalDistance;

    public FindWalkTargetTask(float walkSpeed) {
        this(walkSpeed, 10, 7);
    }

    public FindWalkTargetTask(float walkSpeed, int maxHorizontalDistance, int maxVerticalDistance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.walkSpeed = walkSpeed;
        this.maxHorizontalDistance = maxHorizontalDistance;
        this.maxVerticalDistance = maxVerticalDistance;
    }

    @Override
    protected void run(ServerWorld arg, PathAwareEntity arg2, long l) {
        BlockPos lv = arg2.getBlockPos();
        if (arg.isNearOccupiedPointOfInterest(lv)) {
            this.updateWalkTarget(arg2);
        } else {
            ChunkSectionPos lv2 = ChunkSectionPos.from(lv);
            ChunkSectionPos lv3 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(arg, lv2, 2);
            if (lv3 != lv2) {
                this.updateWalkTarget(arg2, lv3);
            } else {
                this.updateWalkTarget(arg2);
            }
        }
    }

    private void updateWalkTarget(PathAwareEntity entity, ChunkSectionPos pos) {
        Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findTargetTowards(entity, this.maxHorizontalDistance, this.maxVerticalDistance, Vec3d.ofBottomCenter(pos.getCenterPos())));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(arg -> new WalkTarget((Vec3d)arg, this.walkSpeed, 0)));
    }

    private void updateWalkTarget(PathAwareEntity entity) {
        Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(entity, this.maxHorizontalDistance, this.maxVerticalDistance));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(arg -> new WalkTarget((Vec3d)arg, this.walkSpeed, 0)));
    }
}

