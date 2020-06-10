/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
    private final Long2ObjectMap<class_5397> foundPositionsToExpiry = new Long2ObjectOpenHashMap();

    public FindPointOfInterestTask(PointOfInterestType arg, MemoryModuleType<GlobalPos> arg2, MemoryModuleType<GlobalPos> arg3, boolean bl) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)FindPointOfInterestTask.method_29245(arg2, arg3));
        this.poiType = arg;
        this.targetMemoryModuleType = arg3;
        this.onlyRunIfChild = bl;
    }

    public FindPointOfInterestTask(PointOfInterestType arg, MemoryModuleType<GlobalPos> arg2, boolean bl) {
        this(arg, arg2, arg2, bl);
    }

    private static ImmutableMap<MemoryModuleType<?>, MemoryModuleState> method_29245(MemoryModuleType<GlobalPos> arg, MemoryModuleType<GlobalPos> arg2) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(arg, (Object)MemoryModuleState.VALUE_ABSENT);
        if (arg2 != arg) {
            builder.put(arg2, (Object)MemoryModuleState.VALUE_ABSENT);
        }
        return builder.build();
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntityWithAi arg2) {
        if (this.onlyRunIfChild && arg2.isBaby()) {
            return false;
        }
        if (this.positionExpireTimeLimit == 0L) {
            this.positionExpireTimeLimit = arg2.world.getTime() + (long)arg.random.nextInt(20);
            return false;
        }
        return arg.getTime() >= this.positionExpireTimeLimit;
    }

    @Override
    protected void run(ServerWorld arg2, MobEntityWithAi arg22, long l) {
        this.positionExpireTimeLimit = l + 20L + (long)arg2.getRandom().nextInt(20);
        PointOfInterestStorage lv = arg2.getPointOfInterestStorage();
        this.foundPositionsToExpiry.long2ObjectEntrySet().removeIf(entry -> !((class_5397)entry.getValue()).method_29927(l));
        Predicate<BlockPos> predicate = arg -> {
            class_5397 lv = (class_5397)this.foundPositionsToExpiry.get(arg.asLong());
            if (lv == null) {
                return true;
            }
            if (!lv.method_29928(l)) {
                return false;
            }
            lv.method_29926(l);
            return true;
        };
        Set<BlockPos> set = lv.getPositions(this.poiType.getCompletionCondition(), predicate, arg22.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        Path lv2 = arg22.getNavigation().method_29934(set, this.poiType.getSearchDistance());
        if (lv2 != null && lv2.reachesTarget()) {
            BlockPos lv3 = lv2.getTarget();
            lv.getType(lv3).ifPresent(arg5 -> {
                lv.getPosition(this.poiType.getCompletionCondition(), arg2 -> arg2.equals(lv3), lv3, 1);
                arg22.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(arg2.getRegistryKey(), lv3));
                this.foundPositionsToExpiry.clear();
                DebugInfoSender.sendPointOfInterest(arg2, lv3);
            });
        } else {
            for (BlockPos lv4 : set) {
                this.foundPositionsToExpiry.computeIfAbsent(lv4.asLong(), m -> new class_5397(arg.world.random, l));
            }
        }
    }

    static class class_5397 {
        private final Random field_25600;
        private long field_25601;
        private long field_25602;
        private int field_25603;

        class_5397(Random random, long l) {
            this.field_25600 = random;
            this.method_29926(l);
        }

        public void method_29926(long l) {
            this.field_25601 = l;
            int i = this.field_25603 + this.field_25600.nextInt(40) + 40;
            this.field_25603 = Math.min(i, 400);
            this.field_25602 = l + (long)this.field_25603;
        }

        public boolean method_29927(long l) {
            return l - this.field_25601 < 400L;
        }

        public boolean method_29928(long l) {
            return l >= this.field_25602;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.field_25601 + ", nextScheduledAttemptAt=" + this.field_25602 + ", currentDelay=" + this.field_25603 + '}';
        }
    }
}

