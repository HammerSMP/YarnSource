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
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class NearestPlayersSensor
extends Sensor<LivingEntity> {
    private static final TargetPredicate field_25161 = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules();

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        List list = world.getPlayers().stream().filter(EntityPredicates.EXCEPT_SPECTATOR).filter(arg2 -> entity.squaredDistanceTo((Entity)arg2) < 256.0).sorted(Comparator.comparingDouble(entity::squaredDistanceTo)).collect(Collectors.toList());
        Brain<?> lv = entity.getBrain();
        lv.remember(MemoryModuleType.NEAREST_PLAYERS, list);
        List list2 = list.stream().filter(arg2 -> field_25161.test(entity, (LivingEntity)arg2)).collect(Collectors.toList());
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_PLAYER, list2.isEmpty() ? null : (PlayerEntity)list2.get(0));
        Optional<Entity> optional = list2.stream().filter(EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL).findFirst();
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, optional);
    }
}

