/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class NearestItemsSensor
extends Sensor<MobEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
    }

    @Override
    protected void sense(ServerWorld arg3, MobEntity arg22) {
        Brain<?> lv = arg22.getBrain();
        List<ItemEntity> list = arg3.getEntitiesByClass(ItemEntity.class, arg22.getBoundingBox().expand(8.0, 4.0, 8.0), arg -> true);
        list.sort(Comparator.comparingDouble(arg22::squaredDistanceTo));
        Optional<ItemEntity> optional = list.stream().filter(arg2 -> arg22.canGather(arg2.getStack())).filter(arg2 -> arg2.isInRange(arg22, 9.0)).filter(arg22::canSee).findFirst();
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
    }
}

