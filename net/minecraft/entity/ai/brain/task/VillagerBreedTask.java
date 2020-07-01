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
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerBreedTask
extends Task<VillagerEntity> {
    private long breedEndTime;

    public VillagerBreedTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.BREED_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), 350, 350);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        return this.isReadyToBreed(arg2);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return l <= this.breedEndTime && this.isReadyToBreed(arg2);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        PassiveEntity lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(arg2, lv, 0.5f);
        arg.sendEntityStatus(lv, (byte)18);
        arg.sendEntityStatus(arg2, (byte)18);
        int i = 275 + arg2.getRandom().nextInt(50);
        this.breedEndTime = l + (long)i;
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        VillagerEntity lv = (VillagerEntity)arg2.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
        if (arg2.squaredDistanceTo(lv) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(arg2, lv, 0.5f);
        if (l >= this.breedEndTime) {
            arg2.eatForBreeding();
            lv.eatForBreeding();
            this.goHome(arg, arg2, lv);
        } else if (arg2.getRandom().nextInt(35) == 0) {
            arg.sendEntityStatus(lv, (byte)12);
            arg.sendEntityStatus(arg2, (byte)12);
        }
    }

    private void goHome(ServerWorld arg, VillagerEntity arg2, VillagerEntity arg3) {
        Optional<BlockPos> optional = this.getReachableHome(arg, arg2);
        if (!optional.isPresent()) {
            arg.sendEntityStatus(arg3, (byte)13);
            arg.sendEntityStatus(arg2, (byte)13);
        } else {
            Optional<VillagerEntity> optional2 = this.createChild(arg, arg2, arg3);
            if (optional2.isPresent()) {
                this.setChildHome(arg, optional2.get(), optional.get());
            } else {
                arg.getPointOfInterestStorage().releaseTicket(optional.get());
                DebugInfoSender.sendPointOfInterest(arg, optional.get());
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        arg2.getBrain().forget(MemoryModuleType.BREED_TARGET);
    }

    private boolean isReadyToBreed(VillagerEntity arg2) {
        Brain<VillagerEntity> lv = arg2.getBrain();
        Optional<PassiveEntity> optional = lv.getOptionalMemory(MemoryModuleType.BREED_TARGET).filter(arg -> arg.getType() == EntityType.VILLAGER);
        if (!optional.isPresent()) {
            return false;
        }
        return LookTargetUtil.canSee(lv, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && arg2.isReadyToBreed() && optional.get().isReadyToBreed();
    }

    private Optional<BlockPos> getReachableHome(ServerWorld arg, VillagerEntity arg22) {
        return arg.getPointOfInterestStorage().getPosition(PointOfInterestType.HOME.getCompletionCondition(), arg2 -> this.canReachHome(arg22, (BlockPos)arg2), arg22.getBlockPos(), 48);
    }

    private boolean canReachHome(VillagerEntity arg, BlockPos arg2) {
        Path lv = arg.getNavigation().findPathTo(arg2, PointOfInterestType.HOME.getSearchDistance());
        return lv != null && lv.reachesTarget();
    }

    private Optional<VillagerEntity> createChild(ServerWorld arg, VillagerEntity arg2, VillagerEntity arg3) {
        VillagerEntity lv = arg2.createChild(arg, arg3);
        if (lv == null) {
            return Optional.empty();
        }
        arg2.setBreedingAge(6000);
        arg3.setBreedingAge(6000);
        lv.setBreedingAge(-24000);
        lv.refreshPositionAndAngles(arg2.getX(), arg2.getY(), arg2.getZ(), 0.0f, 0.0f);
        arg2.world.spawnEntity(lv);
        arg2.world.sendEntityStatus(lv, (byte)12);
        return Optional.of(lv);
    }

    private void setChildHome(ServerWorld arg, VillagerEntity arg2, BlockPos arg3) {
        GlobalPos lv = GlobalPos.create(arg.getRegistryKey(), arg3);
        arg2.getBrain().remember(MemoryModuleType.HOME, lv);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld arg, LivingEntity arg2, long l) {
        this.run(arg, (VillagerEntity)arg2, l);
    }
}

