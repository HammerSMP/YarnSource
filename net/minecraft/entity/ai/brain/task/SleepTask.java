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
import java.util.Objects;
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
import net.minecraft.util.dynamic.Timestamp;
import net.minecraft.util.math.BlockPos;

public class SleepTask
extends Task<LivingEntity> {
    private long startTime;

    public SleepTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.LAST_WOKEN, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        long l;
        if (arg2.hasVehicle()) {
            return false;
        }
        Brain<?> lv = arg2.getBrain();
        GlobalPos lv2 = lv.getOptionalMemory(MemoryModuleType.HOME).get();
        if (!Objects.equals(arg.method_27983(), lv2.getDimension())) {
            return false;
        }
        Optional<Timestamp> optional = lv.getOptionalMemory(MemoryModuleType.LAST_WOKEN);
        if (optional.isPresent() && (l = arg.getTime() - optional.get().getTime()) > 0L && l < 100L) {
            return false;
        }
        BlockState lv3 = arg.getBlockState(lv2.getPos());
        return lv2.getPos().isWithinDistance(arg2.getPos(), 2.0) && lv3.getBlock().isIn(BlockTags.BEDS) && lv3.get(BedBlock.OCCUPIED) == false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        Optional<GlobalPos> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos lv = optional.get().getPos();
        return arg2.getBrain().hasActivity(Activity.REST) && arg2.getY() > (double)lv.getY() + 0.4 && lv.isWithinDistance(arg2.getPos(), 1.14);
    }

    @Override
    protected void run(ServerWorld arg, LivingEntity arg2, long l) {
        if (l > this.startTime) {
            arg2.getBrain().getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> OpenDoorsTask.closeOpenedDoors(arg, (List<BlockPos>)ImmutableList.of(), 0, arg2, arg2.getBrain()));
            arg2.sleep(arg2.getBrain().getOptionalMemory(MemoryModuleType.HOME).get().getPos());
        }
    }

    @Override
    protected boolean isTimeLimitExceeded(long l) {
        return false;
    }

    @Override
    protected void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        if (arg2.isSleeping()) {
            arg2.wakeUp();
            this.startTime = l + 40L;
        }
    }
}

