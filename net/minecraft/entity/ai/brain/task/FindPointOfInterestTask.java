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
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class FindPointOfInterestTask
extends Task<MobEntityWithAi> {
    private final PointOfInterestType poiType;
    private final MemoryModuleType<GlobalPos> targetMemoryModuleType;
    private final boolean onlyRunIfChild;
    private long positionExpireTimeLimit;
    private final Long2LongMap foundPositionsToExpiry = new Long2LongOpenHashMap();
    private int tries;

    public FindPointOfInterestTask(PointOfInterestType arg, MemoryModuleType<GlobalPos> arg2, boolean bl) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(arg2, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.poiType = arg;
        this.targetMemoryModuleType = arg2;
        this.onlyRunIfChild = bl;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntityWithAi arg2) {
        if (this.onlyRunIfChild && arg2.isBaby()) {
            return false;
        }
        return arg.getTime() - this.positionExpireTimeLimit >= 20L;
    }

    @Override
    protected void run(ServerWorld arg2, MobEntityWithAi arg22, long l) {
        this.tries = 0;
        this.positionExpireTimeLimit = arg2.getTime() + (long)arg2.getRandom().nextInt(20);
        PointOfInterestStorage lv = arg2.getPointOfInterestStorage();
        Predicate<BlockPos> predicate = arg -> {
            long l = arg.asLong();
            if (this.foundPositionsToExpiry.containsKey(l)) {
                return false;
            }
            if (++this.tries >= 5) {
                return false;
            }
            this.foundPositionsToExpiry.put(l, this.positionExpireTimeLimit + 40L);
            return true;
        };
        Stream<BlockPos> stream = lv.getPositions(this.poiType.getCompletionCondition(), predicate, arg22.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
        Path lv2 = arg22.getNavigation().findPathToAny(stream, this.poiType.getSearchDistance());
        if (lv2 != null && lv2.reachesTarget()) {
            BlockPos lv3 = lv2.getTarget();
            lv.getType(lv3).ifPresent(arg5 -> {
                lv.getPosition(this.poiType.getCompletionCondition(), arg2 -> arg2.equals(lv3), lv3, 1);
                arg22.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(arg2.getDimension().getType(), lv3));
                DebugInfoSender.sendPointOfInterest(arg2, lv3);
            });
        } else if (this.tries < 5) {
            this.foundPositionsToExpiry.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.positionExpireTimeLimit);
        }
    }
}

