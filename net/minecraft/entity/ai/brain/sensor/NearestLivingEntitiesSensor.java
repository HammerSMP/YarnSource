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
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;

public class NearestLivingEntitiesSensor
extends Sensor<LivingEntity> {
    private static final TargetPredicate CLOSE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules();

    @Override
    protected void sense(ServerWorld arg, LivingEntity arg22) {
        List<LivingEntity> list = arg.getEntities(LivingEntity.class, arg22.getBoundingBox().expand(16.0, 16.0, 16.0), arg2 -> arg2 != arg22 && arg2.isAlive());
        list.sort(Comparator.comparingDouble(arg22::squaredDistanceTo));
        Brain<?> lv = arg22.getBrain();
        lv.remember(MemoryModuleType.MOBS, list);
        lv.remember(MemoryModuleType.VISIBLE_MOBS, list.stream().filter(arg2 -> CLOSE_ENTITY_PREDICATE.test(arg22, (LivingEntity)arg2)).collect(Collectors.toList()));
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
    }
}

