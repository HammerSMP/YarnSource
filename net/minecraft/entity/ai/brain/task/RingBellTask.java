/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RingBellTask
extends Task<LivingEntity> {
    public RingBellTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return world.random.nextFloat() > 0.95f;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        BlockState lv3;
        Brain<?> lv = entity.getBrain();
        BlockPos lv2 = lv.getOptionalMemory(MemoryModuleType.MEETING_POINT).get().getPos();
        if (lv2.isWithinDistance(entity.getBlockPos(), 3.0) && (lv3 = world.getBlockState(lv2)).isOf(Blocks.BELL)) {
            BellBlock lv4 = (BellBlock)lv3.getBlock();
            lv4.ring(world, lv2, null);
        }
    }
}

