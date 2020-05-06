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
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class SeekSkyTask
extends Task<LivingEntity> {
    private final float speed;

    public SeekSkyTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = f;
    }

    @Override
    protected void run(ServerWorld arg2, LivingEntity arg22, long l) {
        Optional<Vec3d> optional = Optional.ofNullable(this.findNearbySky(arg2, arg22));
        if (optional.isPresent()) {
            arg22.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(arg -> new WalkTarget((Vec3d)arg, this.speed, 0)));
        }
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        return !arg.isSkyVisible(arg2.getBlockPos());
    }

    @Nullable
    private Vec3d findNearbySky(ServerWorld arg, LivingEntity arg2) {
        Random random = arg2.getRandom();
        BlockPos lv = arg2.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            BlockPos lv2 = lv.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (!SeekSkyTask.isSkyVisible(arg, arg2, lv2)) continue;
            return Vec3d.method_24955(lv2);
        }
        return null;
    }

    public static boolean isSkyVisible(ServerWorld arg, LivingEntity arg2, BlockPos arg3) {
        return arg.isSkyVisible(arg3) && (double)arg.getTopPosition(Heightmap.Type.MOTION_BLOCKING, arg3).getY() <= arg2.getY();
    }
}

