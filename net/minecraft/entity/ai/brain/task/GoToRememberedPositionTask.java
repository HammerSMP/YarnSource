/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GoToRememberedPositionTask<T>
extends Task<PathAwareEntity> {
    private final MemoryModuleType<T> entityMemory;
    private final float speed;
    private final int range;
    private final Function<T, Vec3d> posRetriever;

    public GoToRememberedPositionTask(MemoryModuleType<T> arg, float f, int i, boolean bl, Function<T, Vec3d> function) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)(bl ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT)), arg, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityMemory = arg;
        this.speed = f;
        this.range = i;
        this.posRetriever = function;
    }

    public static GoToRememberedPositionTask<BlockPos> toBlock(MemoryModuleType<BlockPos> arg, float f, int i, boolean bl) {
        return new GoToRememberedPositionTask<BlockPos>(arg, f, i, bl, Vec3d::ofBottomCenter);
    }

    public static GoToRememberedPositionTask<? extends Entity> toEntity(MemoryModuleType<? extends Entity> arg, float f, int i, boolean bl) {
        return new GoToRememberedPositionTask<Entity>(arg, f, i, bl, Entity::getPos);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, PathAwareEntity arg2) {
        if (this.isWalkTargetPresentAndFar(arg2)) {
            return false;
        }
        return arg2.getPos().isInRange(this.getPos(arg2), this.range);
    }

    private Vec3d getPos(PathAwareEntity arg) {
        return this.posRetriever.apply(arg.getBrain().getOptionalMemory(this.entityMemory).get());
    }

    private boolean isWalkTargetPresentAndFar(PathAwareEntity arg) {
        Vec3d lv3;
        if (!arg.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
            return false;
        }
        WalkTarget lv = arg.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (lv.getSpeed() != this.speed) {
            return false;
        }
        Vec3d lv2 = lv.getLookTarget().getPos().subtract(arg.getPos());
        return lv2.dotProduct(lv3 = this.getPos(arg).subtract(arg.getPos())) < 0.0;
    }

    @Override
    protected void run(ServerWorld arg, PathAwareEntity arg2, long l) {
        GoToRememberedPositionTask.setWalkTarget(arg2, this.getPos(arg2), this.speed);
    }

    private static void setWalkTarget(PathAwareEntity arg, Vec3d arg2, float f) {
        for (int i = 0; i < 10; ++i) {
            Vec3d lv = TargetFinder.findGroundTargetAwayFrom(arg, 16, 7, arg2);
            if (lv == null) continue;
            arg.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lv, f, 0));
            return;
        }
    }
}

