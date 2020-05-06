/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FollowCustomerTask
extends Task<VillagerEntity> {
    private final float speed;

    public FollowCustomerTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)), Integer.MAX_VALUE);
        this.speed = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        PlayerEntity lv = arg2.getCurrentCustomer();
        return arg2.isAlive() && lv != null && !arg2.isTouchingWater() && !arg2.velocityModified && arg2.squaredDistanceTo(lv) <= 16.0 && lv.currentScreenHandler != null;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return this.shouldRun(arg, arg2);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        this.update(arg2);
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        Brain<VillagerEntity> lv = arg2.getBrain();
        lv.forget(MemoryModuleType.WALK_TARGET);
        lv.forget(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        this.update(arg2);
    }

    @Override
    protected boolean isTimeLimitExceeded(long l) {
        return false;
    }

    private void update(VillagerEntity arg) {
        Brain<VillagerEntity> lv = arg.getBrain();
        lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(arg.getCurrentCustomer(), false), this.speed, 2));
        lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(arg.getCurrentCustomer(), true));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld arg, LivingEntity arg2, long l) {
        this.run(arg, (VillagerEntity)arg2, l);
    }
}

