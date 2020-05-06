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
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask
extends Task<MobEntityWithAi> {
    private final float walkSpeed;
    private final int maxHorizontalDistance;
    private final int maxVerticalDistance;

    public FindWalkTargetTask(float f) {
        this(f, 10, 7);
    }

    public FindWalkTargetTask(float f, int i, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.walkSpeed = f;
        this.maxHorizontalDistance = i;
        this.maxVerticalDistance = j;
    }

    @Override
    protected void run(ServerWorld arg, MobEntityWithAi arg2, long l) {
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

    private void updateWalkTarget(MobEntityWithAi arg2, ChunkSectionPos arg22) {
        Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findTargetTowards(arg2, this.maxHorizontalDistance, this.maxVerticalDistance, Vec3d.method_24955(arg22.getCenterPos())));
        arg2.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(arg -> new WalkTarget((Vec3d)arg, this.walkSpeed, 0)));
    }

    private void updateWalkTarget(MobEntityWithAi arg2) {
        Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(arg2, this.maxHorizontalDistance, this.maxVerticalDistance));
        arg2.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(arg -> new WalkTarget((Vec3d)arg, this.walkSpeed, 0)));
    }
}

