/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class SleepTask
extends Task<LivingEntity> {
    private long startTime;

    public SleepTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.LAST_WOKEN, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        long l;
        if (entity.hasVehicle()) {
            return false;
        }
        Brain<?> lv = entity.getBrain();
        GlobalPos lv2 = lv.getOptionalMemory(MemoryModuleType.HOME).get();
        if (world.getRegistryKey() != lv2.getDimension()) {
            return false;
        }
        Optional<Long> optional = lv.getOptionalMemory(MemoryModuleType.LAST_WOKEN);
        if (optional.isPresent() && (l = world.getTime() - optional.get()) > 0L && l < 100L) {
            return false;
        }
        BlockState lv3 = world.getBlockState(lv2.getPos());
        return lv2.getPos().isWithinDistance(entity.getPos(), 2.0) && lv3.getBlock().isIn(BlockTags.BEDS) && lv3.get(BedBlock.OCCUPIED) == false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        Optional<GlobalPos> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos lv = optional.get().getPos();
        return entity.getBrain().hasActivity(Activity.REST) && entity.getY() > (double)lv.getY() + 0.4 && lv.isWithinDistance(entity.getPos(), 1.14);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        if (time > this.startTime) {
            entity.getBrain().getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> OpenDoorsTask.closeOpenedDoors(world, (List<BlockPos>)ImmutableList.of(), 0, entity, entity.getBrain()));
            entity.sleep(entity.getBrain().getOptionalMemory(MemoryModuleType.HOME).get().getPos());
        }
    }

    @Override
    protected boolean isTimeLimitExceeded(long time) {
        return false;
    }

    @Override
    protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        if (entity.isSleeping()) {
            entity.wakeUp();
            this.startTime = time + 40L;
        }
    }
}

