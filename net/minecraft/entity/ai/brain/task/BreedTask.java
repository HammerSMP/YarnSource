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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class BreedTask
extends Task<AnimalEntity> {
    private final EntityType<? extends AnimalEntity> targetType;
    private final float field_23129;
    private long breedTime;

    public BreedTask(EntityType<? extends AnimalEntity> arg, float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.BREED_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)), 325);
        this.targetType = arg;
        this.field_23129 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, AnimalEntity arg2) {
        return arg2.isInLove() && this.findBreedTarget(arg2).isPresent();
    }

    @Override
    protected void run(ServerWorld arg, AnimalEntity arg2, long l) {
        AnimalEntity lv = this.findBreedTarget(arg2).get();
        arg2.getBrain().remember(MemoryModuleType.BREED_TARGET, lv);
        lv.getBrain().remember(MemoryModuleType.BREED_TARGET, arg2);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(arg2, lv, this.field_23129);
        int i = 275 + arg2.getRandom().nextInt(50);
        this.breedTime = l + (long)i;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, AnimalEntity arg2, long l) {
        if (!this.hasBreedTarget(arg2)) {
            return false;
        }
        AnimalEntity lv = this.getBreedTarget(arg2);
        return lv.isAlive() && arg2.canBreedWith(lv) && LookTargetUtil.canSee(arg2.getBrain(), lv) && l <= this.breedTime;
    }

    @Override
    protected void keepRunning(ServerWorld arg, AnimalEntity arg2, long l) {
        AnimalEntity lv = this.getBreedTarget(arg2);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(arg2, lv, this.field_23129);
        if (!arg2.isInRange(lv, 3.0)) {
            return;
        }
        if (l >= this.breedTime) {
            arg2.breed(arg, lv);
            arg2.getBrain().forget(MemoryModuleType.BREED_TARGET);
            lv.getBrain().forget(MemoryModuleType.BREED_TARGET);
        }
    }

    @Override
    protected void finishRunning(ServerWorld arg, AnimalEntity arg2, long l) {
        arg2.getBrain().forget(MemoryModuleType.BREED_TARGET);
        arg2.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg2.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        this.breedTime = 0L;
    }

    private AnimalEntity getBreedTarget(AnimalEntity arg) {
        return (AnimalEntity)arg.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean hasBreedTarget(AnimalEntity arg) {
        Brain<PassiveEntity> lv = arg.getBrain();
        return lv.hasMemoryModule(MemoryModuleType.BREED_TARGET) && lv.getOptionalMemory(MemoryModuleType.BREED_TARGET).get().getType() == this.targetType;
    }

    private Optional<? extends AnimalEntity> findBreedTarget(AnimalEntity arg2) {
        return arg2.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().filter(arg -> arg.getType() == this.targetType).map(arg -> (AnimalEntity)arg).filter(arg2::canBreedWith).findFirst();
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (AnimalEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (AnimalEntity)arg2, l);
    }
}

