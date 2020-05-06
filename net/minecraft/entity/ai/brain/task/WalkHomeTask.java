/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WalkHomeTask
extends Task<LivingEntity> {
    private final float speed;
    private final Long2LongMap positionToExpiry = new Long2LongOpenHashMap();
    private int tries;
    private long expiryTimeLimit;

    public WalkHomeTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        if (arg.getTime() - this.expiryTimeLimit < 20L) {
            return false;
        }
        MobEntityWithAi lv = (MobEntityWithAi)arg2;
        PointOfInterestStorage lv2 = arg.getPointOfInterestStorage();
        Optional<BlockPos> optional = lv2.getNearestPosition(PointOfInterestType.HOME.getCompletionCondition(), arg2.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        return optional.isPresent() && !(optional.get().getSquaredDistance(lv.getBlockPos()) <= 4.0);
    }

    @Override
    protected void run(ServerWorld arg2, LivingEntity arg22, long l) {
        this.tries = 0;
        this.expiryTimeLimit = arg2.getTime() + (long)arg2.getRandom().nextInt(20);
        MobEntityWithAi lv = (MobEntityWithAi)arg22;
        PointOfInterestStorage lv2 = arg2.getPointOfInterestStorage();
        Predicate<BlockPos> predicate = arg -> {
            long l = arg.asLong();
            if (this.positionToExpiry.containsKey(l)) {
                return false;
            }
            if (++this.tries >= 5) {
                return false;
            }
            this.positionToExpiry.put(l, this.expiryTimeLimit + 40L);
            return true;
        };
        Stream<BlockPos> stream = lv2.getPositions(PointOfInterestType.HOME.getCompletionCondition(), predicate, arg22.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        Path lv3 = lv.getNavigation().findPathToAny(stream, PointOfInterestType.HOME.getSearchDistance());
        if (lv3 != null && lv3.reachesTarget()) {
            BlockPos lv4 = lv3.getTarget();
            Optional<PointOfInterestType> optional = lv2.getType(lv4);
            if (optional.isPresent()) {
                arg22.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lv4, this.speed, 1));
                DebugInfoSender.sendPointOfInterest(arg2, lv4);
            }
        } else if (this.tries < 5) {
            this.positionToExpiry.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTimeLimit);
        }
    }
}

