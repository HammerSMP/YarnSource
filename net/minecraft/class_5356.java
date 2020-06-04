/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class class_5356
extends Sensor<PassiveEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_MOBS);
    }

    @Override
    protected void sense(ServerWorld arg, PassiveEntity arg2) {
        arg2.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> this.method_29529(arg2, (List<LivingEntity>)list));
    }

    private void method_29529(PassiveEntity arg3, List<LivingEntity> list) {
        Optional<PassiveEntity> optional = list.stream().filter(arg2 -> arg2.getType() == arg3.getType()).map(arg -> (PassiveEntity)arg).filter(arg -> !arg.isBaby()).findFirst();
        arg3.getBrain().remember(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
    }
}

