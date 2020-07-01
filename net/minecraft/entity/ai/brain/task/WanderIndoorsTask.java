/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class WanderIndoorsTask
extends Task<PathAwareEntity> {
    private final float speed;

    public WanderIndoorsTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, PathAwareEntity arg2) {
        return !arg.isSkyVisible(arg2.getBlockPos());
    }

    @Override
    protected void run(ServerWorld arg, PathAwareEntity arg22, long l) {
        BlockPos lv = arg22.getBlockPos();
        List list = BlockPos.stream(lv.add(-1, -1, -1), lv.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Collectors.toList());
        Collections.shuffle(list);
        Optional<BlockPos> optional = list.stream().filter(arg2 -> !arg.isSkyVisible((BlockPos)arg2)).filter(arg3 -> arg.isTopSolid((BlockPos)arg3, arg22)).filter(arg3 -> arg.doesNotCollide(arg22)).findFirst();
        optional.ifPresent(arg2 -> arg22.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget((BlockPos)arg2, this.speed, 0)));
    }
}

