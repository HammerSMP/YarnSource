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
import net.minecraft.entity.mob.PathAwareEntity;
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

    public WalkHomeTask(float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        if (world.getTime() - this.expiryTimeLimit < 20L) {
            return false;
        }
        PathAwareEntity lv = (PathAwareEntity)entity;
        PointOfInterestStorage lv2 = world.getPointOfInterestStorage();
        Optional<BlockPos> optional = lv2.getNearestPosition(PointOfInterestType.HOME.getCompletionCondition(), entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        return optional.isPresent() && !(optional.get().getSquaredDistance(lv.getBlockPos()) <= 4.0);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        this.tries = 0;
        this.expiryTimeLimit = world.getTime() + (long)world.getRandom().nextInt(20);
        PathAwareEntity lv = (PathAwareEntity)entity;
        PointOfInterestStorage lv2 = world.getPointOfInterestStorage();
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
        Stream<BlockPos> stream = lv2.getPositions(PointOfInterestType.HOME.getCompletionCondition(), predicate, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        Path lv3 = lv.getNavigation().findPathToAny(stream, PointOfInterestType.HOME.getSearchDistance());
        if (lv3 != null && lv3.reachesTarget()) {
            BlockPos lv4 = lv3.getTarget();
            Optional<PointOfInterestType> optional = lv2.getType(lv4);
            if (optional.isPresent()) {
                entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lv4, this.speed, 1));
                DebugInfoSender.sendPointOfInterest(world, lv4);
            }
        } else if (this.tries < 5) {
            this.positionToExpiry.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTimeLimit);
        }
    }
}

