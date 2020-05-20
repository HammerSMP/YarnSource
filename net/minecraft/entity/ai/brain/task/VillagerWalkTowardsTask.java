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
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VillagerWalkTowardsTask
extends Task<VillagerEntity> {
    private final MemoryModuleType<GlobalPos> destination;
    private final float speed;
    private final int completionRange;
    private final int maxRange;
    private final int maxRunTime;

    public VillagerWalkTowardsTask(MemoryModuleType<GlobalPos> arg, float f, int i, int j, int k) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), arg, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.destination = arg;
        this.speed = f;
        this.completionRange = i;
        this.maxRange = j;
        this.maxRunTime = k;
    }

    private void giveUp(VillagerEntity arg, long l) {
        Brain<VillagerEntity> lv = arg.getBrain();
        arg.releaseTicketFor(this.destination);
        lv.forget(this.destination);
        lv.remember(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, l);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        Brain<VillagerEntity> lv = arg2.getBrain();
        lv.getOptionalMemory(this.destination).ifPresent(arg4 -> {
            if (this.shouldGiveUp(arg, arg2)) {
                this.giveUp(arg2, l);
            } else if (this.exceedsMaxRange(arg, arg2, (GlobalPos)arg4)) {
                int i;
                Vec3d lv = null;
                int j = 1000;
                for (i = 0; i < 1000 && (lv == null || this.exceedsMaxRange(arg, arg2, GlobalPos.create(arg.method_27983(), new BlockPos(lv)))); ++i) {
                    lv = TargetFinder.findTargetTowards(arg2, 15, 7, Vec3d.ofBottomCenter(arg4.getPos()));
                }
                if (i == 1000) {
                    this.giveUp(arg2, l);
                    return;
                }
                lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lv, this.speed, this.completionRange));
            } else if (!this.reachedDestination(arg, arg2, (GlobalPos)arg4)) {
                lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(arg4.getPos(), this.speed, this.completionRange));
            }
        });
    }

    private boolean shouldGiveUp(ServerWorld arg, VillagerEntity arg2) {
        Optional<Long> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        if (optional.isPresent()) {
            return arg.getTime() - optional.get() > (long)this.maxRunTime;
        }
        return false;
    }

    private boolean exceedsMaxRange(ServerWorld arg, VillagerEntity arg2, GlobalPos arg3) {
        return arg3.getDimension() != arg.method_27983() || arg3.getPos().getManhattanDistance(arg2.getBlockPos()) > this.maxRange;
    }

    private boolean reachedDestination(ServerWorld arg, VillagerEntity arg2, GlobalPos arg3) {
        return arg3.getDimension() == arg.method_27983() && arg3.getPos().getManhattanDistance(arg2.getBlockPos()) <= this.completionRange;
    }
}

