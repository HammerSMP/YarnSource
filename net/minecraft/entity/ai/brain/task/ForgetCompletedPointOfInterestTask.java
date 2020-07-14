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

    public ForgetCompletedPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> memoryModule) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(memoryModule, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.condition = poiType.getCompletionCondition();
        this.memoryModule = memoryModule;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        GlobalPos lv = entity.getBrain().getOptionalMemory(this.memoryModule).get();
        return world.getRegistryKey() == lv.getDimension() && lv.getPos().isWithinDistance(entity.getPos(), 16.0);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Brain<?> lv = entity.getBrain();
        GlobalPos lv2 = lv.getOptionalMemory(this.memoryModule).get();
        BlockPos lv3 = lv2.getPos();
        ServerWorld lv4 = world.getServer().getWorld(lv2.getDimension());
        if (lv4 == null || this.hasCompletedPointOfInterest(lv4, lv3)) {
            lv.forget(this.memoryModule);
        } else if (this.isBedOccupiedByOthers(lv4, lv3, entity)) {
            lv.forget(this.memoryModule);
            world.getPointOfInterestStorage().releaseTicket(lv3);
            DebugInfoSender.sendPointOfInterest(world, lv3);
        }
    }

    private boolean isBedOccupiedByOthers(ServerWorld world, BlockPos pos, LivingEntity entity) {
        BlockState lv = world.getBlockState(pos);
        return lv.getBlock().isIn(BlockTags.BEDS) && lv.get(BedBlock.OCCUPIED) != false && !entity.isSleeping();
    }

    private boolean hasCompletedPointOfInterest(ServerWorld world, BlockPos pos) {
        return !world.getPointOfInterestStorage().test(pos, this.condition);
    }
}

