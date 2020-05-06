/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class NearestBedSensor
extends Sensor<MobEntity> {
    private final Long2LongMap positionToExpiryTime = new Long2LongOpenHashMap();
    private int tries;
    private long expiryTime;

    public NearestBedSensor() {
        super(20);
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }

    @Override
    protected void sense(ServerWorld arg2, MobEntity arg22) {
        if (!arg22.isBaby()) {
            return;
        }
        this.tries = 0;
        this.expiryTime = arg2.getTime() + (long)arg2.getRandom().nextInt(20);
        PointOfInterestStorage lv = arg2.getPointOfInterestStorage();
        Predicate<BlockPos> predicate = arg -> {
            long l = arg.asLong();
            if (this.positionToExpiryTime.containsKey(l)) {
                return false;
            }
            if (++this.tries >= 5) {
                return false;
            }
            this.positionToExpiryTime.put(l, this.expiryTime + 40L);
            return true;
        };
        Stream<BlockPos> stream = lv.getPositions(PointOfInterestType.HOME.getCompletionCondition(), predicate, arg22.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        Path lv2 = arg22.getNavigation().findPathToAny(stream, PointOfInterestType.HOME.getSearchDistance());
        if (lv2 != null && lv2.reachesTarget()) {
            BlockPos lv3 = lv2.getTarget();
            Optional<PointOfInterestType> optional = lv.getType(lv3);
            if (optional.isPresent()) {
                arg22.getBrain().remember(MemoryModuleType.NEAREST_BED, lv3);
            }
        } else if (this.tries < 5) {
            this.positionToExpiryTime.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTime);
        }
    }
}

