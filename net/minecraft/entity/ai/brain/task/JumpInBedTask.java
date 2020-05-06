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
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class JumpInBedTask
extends Task<MobEntity> {
    private final float walkSpeed;
    @Nullable
    private BlockPos bedPos;
    private int ticksOutOfBedUntilStopped;
    private int jumpsRemaining;
    private int ticksToNextJump;

    public JumpInBedTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_BED, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.walkSpeed = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntity arg2) {
        return arg2.isBaby() && this.shouldStartJumping(arg, arg2);
    }

    @Override
    protected void run(ServerWorld arg, MobEntity arg2, long l) {
        super.run(arg, arg2, l);
        this.getNearestBed(arg2).ifPresent(arg3 -> {
            this.bedPos = arg3;
            this.ticksOutOfBedUntilStopped = 100;
            this.jumpsRemaining = 3 + arg.random.nextInt(4);
            this.ticksToNextJump = 0;
            this.setWalkTarget(arg2, (BlockPos)arg3);
        });
    }

    @Override
    protected void finishRunning(ServerWorld arg, MobEntity arg2, long l) {
        super.finishRunning(arg, arg2, l);
        this.bedPos = null;
        this.ticksOutOfBedUntilStopped = 0;
        this.jumpsRemaining = 0;
        this.ticksToNextJump = 0;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, MobEntity arg2, long l) {
        return arg2.isBaby() && this.bedPos != null && this.isBedAt(arg, this.bedPos) && !this.isBedGoneTooLong(arg, arg2) && !this.isDoneJumping(arg, arg2);
    }

    @Override
    protected boolean isTimeLimitExceeded(long l) {
        return false;
    }

    @Override
    protected void keepRunning(ServerWorld arg, MobEntity arg2, long l) {
        if (!this.isAboveBed(arg, arg2)) {
            --this.ticksOutOfBedUntilStopped;
            return;
        }
        if (this.ticksToNextJump > 0) {
            --this.ticksToNextJump;
            return;
        }
        if (this.isOnBed(arg, arg2)) {
            arg2.getJumpControl().setActive();
            --this.jumpsRemaining;
            this.ticksToNextJump = 5;
        }
    }

    private void setWalkTarget(MobEntity arg, BlockPos arg2) {
        arg.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(arg2, this.walkSpeed, 0));
    }

    private boolean shouldStartJumping(ServerWorld arg, MobEntity arg2) {
        return this.isAboveBed(arg, arg2) || this.getNearestBed(arg2).isPresent();
    }

    private boolean isAboveBed(ServerWorld arg, MobEntity arg2) {
        BlockPos lv = arg2.getBlockPos();
        BlockPos lv2 = lv.down();
        return this.isBedAt(arg, lv) || this.isBedAt(arg, lv2);
    }

    private boolean isOnBed(ServerWorld arg, MobEntity arg2) {
        return this.isBedAt(arg, arg2.getBlockPos());
    }

    private boolean isBedAt(ServerWorld arg, BlockPos arg2) {
        return arg.getBlockState(arg2).isIn(BlockTags.BEDS);
    }

    private Optional<BlockPos> getNearestBed(MobEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_BED);
    }

    private boolean isBedGoneTooLong(ServerWorld arg, MobEntity arg2) {
        return !this.isAboveBed(arg, arg2) && this.ticksOutOfBedUntilStopped <= 0;
    }

    private boolean isDoneJumping(ServerWorld arg, MobEntity arg2) {
        return this.isAboveBed(arg, arg2) && this.jumpsRemaining <= 0;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (MobEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (MobEntity)arg2, l);
    }
}

