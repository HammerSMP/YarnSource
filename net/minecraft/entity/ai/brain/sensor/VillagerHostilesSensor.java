/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class VillagerHostilesSensor
extends Sensor<LivingEntity> {
    private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.builder().put(EntityType.DROWNED, (Object)Float.valueOf(8.0f)).put(EntityType.EVOKER, (Object)Float.valueOf(12.0f)).put(EntityType.HUSK, (Object)Float.valueOf(8.0f)).put(EntityType.ILLUSIONER, (Object)Float.valueOf(12.0f)).put(EntityType.PILLAGER, (Object)Float.valueOf(15.0f)).put(EntityType.RAVAGER, (Object)Float.valueOf(12.0f)).put(EntityType.VEX, (Object)Float.valueOf(8.0f)).put(EntityType.VINDICATOR, (Object)Float.valueOf(10.0f)).put(EntityType.ZOGLIN, (Object)Float.valueOf(10.0f)).put(EntityType.ZOMBIE, (Object)Float.valueOf(8.0f)).put(EntityType.ZOMBIE_VILLAGER, (Object)Float.valueOf(8.0f)).build();

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }

    @Override
    protected void sense(ServerWorld arg, LivingEntity arg2) {
        arg2.getBrain().remember(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(arg2));
    }

    private Optional<LivingEntity> getNearestHostile(LivingEntity arg) {
        return this.getVisibleMobs(arg).flatMap(list -> list.stream().filter(this::isHostile).filter(arg2 -> this.isCloseEnoughForDanger(arg, (LivingEntity)arg2)).min((arg2, arg3) -> this.compareDistances(arg, (LivingEntity)arg2, (LivingEntity)arg3)));
    }

    private Optional<List<LivingEntity>> getVisibleMobs(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
    }

    private int compareDistances(LivingEntity arg, LivingEntity arg2, LivingEntity arg3) {
        return MathHelper.floor(arg2.squaredDistanceTo(arg) - arg3.squaredDistanceTo(arg));
    }

    private boolean isCloseEnoughForDanger(LivingEntity arg, LivingEntity arg2) {
        float f = ((Float)SQUARED_DISTANCES_FOR_DANGER.get(arg2.getType())).floatValue();
        return arg2.squaredDistanceTo(arg) <= (double)(f * f);
    }

    private boolean isHostile(LivingEntity arg) {
        return SQUARED_DISTANCES_FOR_DANGER.containsKey(arg.getType());
    }
}

