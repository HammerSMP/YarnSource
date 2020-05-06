/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;

public class GoToPointOfInterestTask
extends Task<VillagerEntity> {
    private final float speed;
    private final int completionRange;

    public GoToPointOfInterestTask(float f, int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = f;
        this.completionRange = i;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        return !arg.isNearOccupiedPointOfInterest(arg2.getBlockPos());
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg22, long l) {
        PointOfInterestStorage lv = arg.getPointOfInterestStorage();
        int i = lv.getDistanceFromNearestOccupied(ChunkSectionPos.from(arg22.getBlockPos()));
        Vec3d lv2 = null;
        for (int j = 0; j < 5; ++j) {
            Vec3d lv3 = TargetFinder.findGroundTarget(arg22, 15, 7, arg2 -> -arg.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(arg2)));
            if (lv3 == null) continue;
            int k = lv.getDistanceFromNearestOccupied(ChunkSectionPos.from(new BlockPos(lv3)));
            if (k < i) {
                lv2 = lv3;
                break;
            }
            if (k != i) continue;
            lv2 = lv3;
        }
        if (lv2 != null) {
            arg22.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lv2, this.speed, this.completionRange));
        }
    }
}

