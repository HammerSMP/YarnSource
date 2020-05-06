/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WanderAroundTask
extends Task<MobEntity> {
    @Nullable
    private Path path;
    @Nullable
    private BlockPos lookTargetPos;
    private float speed;
    private int pathUpdateCountdownTicks;

    public WanderAroundTask(int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.PATH, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), i);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntity arg2) {
        Brain<?> lv = arg2.getBrain();
        WalkTarget lv2 = lv.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        boolean bl = this.hasReached(arg2, lv2);
        if (!bl && this.hasFinishedPath(arg2, lv2, arg.getTime())) {
            this.lookTargetPos = lv2.getLookTarget().getBlockPos();
            return true;
        }
        lv.forget(MemoryModuleType.WALK_TARGET);
        if (bl) {
            lv.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, MobEntity arg2, long l) {
        if (this.path == null || this.lookTargetPos == null) {
            return false;
        }
        Optional<WalkTarget> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
        EntityNavigation lv = arg2.getNavigation();
        return !lv.isIdle() && optional.isPresent() && !this.hasReached(arg2, optional.get());
    }

    @Override
    protected void finishRunning(ServerWorld arg, MobEntity arg2, long l) {
        arg2.getNavigation().stop();
        arg2.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg2.getBrain().forget(MemoryModuleType.PATH);
        this.path = null;
    }

    @Override
    protected void run(ServerWorld arg, MobEntity arg2, long l) {
        arg2.getBrain().remember(MemoryModuleType.PATH, this.path);
        arg2.getNavigation().startMovingAlong(this.path, this.speed);
        this.pathUpdateCountdownTicks = arg.getRandom().nextInt(10);
    }

    @Override
    protected void keepRunning(ServerWorld arg, MobEntity arg2, long l) {
        --this.pathUpdateCountdownTicks;
        if (this.pathUpdateCountdownTicks > 0) {
            return;
        }
        Path lv = arg2.getNavigation().getCurrentPath();
        Brain<?> lv2 = arg2.getBrain();
        if (this.path != lv) {
            this.path = lv;
            lv2.remember(MemoryModuleType.PATH, lv);
        }
        if (lv == null || this.lookTargetPos == null) {
            return;
        }
        WalkTarget lv3 = lv2.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (lv3.getLookTarget().getBlockPos().getSquaredDistance(this.lookTargetPos) > 4.0 && this.hasFinishedPath(arg2, lv3, arg.getTime())) {
            this.lookTargetPos = lv3.getLookTarget().getBlockPos();
            this.run(arg, arg2, l);
        }
    }

    private boolean hasFinishedPath(MobEntity arg, WalkTarget arg2, long l) {
        BlockPos lv = arg2.getLookTarget().getBlockPos();
        this.path = arg.getNavigation().findPathTo(lv, 0);
        this.speed = arg2.getSpeed();
        Brain<Long> lv2 = arg.getBrain();
        if (this.hasReached(arg, arg2)) {
            lv2.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        } else {
            boolean bl;
            boolean bl2 = bl = this.path != null && this.path.reachesTarget();
            if (bl) {
                lv2.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (!lv2.hasMemoryModule(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                lv2.remember(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, l);
            }
            if (this.path != null) {
                return true;
            }
            Vec3d lv3 = TargetFinder.findTargetTowards((MobEntityWithAi)arg, 10, 7, Vec3d.method_24955(lv));
            if (lv3 != null) {
                this.path = arg.getNavigation().findPathTo(lv3.x, lv3.y, lv3.z, 0);
                return this.path != null;
            }
        }
        return false;
    }

    private boolean hasReached(MobEntity arg, WalkTarget arg2) {
        return arg2.getLookTarget().getBlockPos().getManhattanDistance(arg.getBlockPos()) <= arg2.getCompletionRange();
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (MobEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (MobEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld arg, LivingEntity arg2, long l) {
        this.run(arg, (MobEntity)arg2, l);
    }
}

