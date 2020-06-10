/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class ForgetCompletedPointOfInterestTask
extends Task<LivingEntity> {
    private final MemoryModuleType<GlobalPos> memoryModule;
    private final Predicate<PointOfInterestType> condition;

    public ForgetCompletedPointOfInterestTask(PointOfInterestType arg, MemoryModuleType<GlobalPos> arg2) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(arg2, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.condition = arg.getCompletionCondition();
        this.memoryModule = arg2;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        GlobalPos lv = arg2.getBrain().getOptionalMemory(this.memoryModule).get();
        return arg.getRegistryKey() == lv.getDimension() && lv.getPos().isWithinDistance(arg2.getPos(), 16.0);
    }

    @Override
    protected void run(ServerWorld arg, LivingEntity arg2, long l) {
        Brain<?> lv = arg2.getBrain();
        GlobalPos lv2 = lv.getOptionalMemory(this.memoryModule).get();
        BlockPos lv3 = lv2.getPos();
        ServerWorld lv4 = arg.getServer().getWorld(lv2.getDimension());
        if (lv4 == null || this.hasCompletedPointOfInterest(lv4, lv3)) {
            lv.forget(this.memoryModule);
        } else if (this.isBedOccupiedByOthers(lv4, lv3, arg2)) {
            lv.forget(this.memoryModule);
            arg.getPointOfInterestStorage().releaseTicket(lv3);
            DebugInfoSender.sendPointOfInterest(arg, lv3);
        }
    }

    private boolean isBedOccupiedByOthers(ServerWorld arg, BlockPos arg2, LivingEntity arg3) {
        BlockState lv = arg.getBlockState(arg2);
        return lv.getBlock().isIn(BlockTags.BEDS) && lv.get(BedBlock.OCCUPIED) != false && !arg3.isSleeping();
    }

    private boolean hasCompletedPointOfInterest(ServerWorld arg, BlockPos arg2) {
        return !arg.getPointOfInterestStorage().test(arg2, this.condition);
    }
}

