/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;

public class GolemLastSeenSensor
extends Sensor<LivingEntity> {
    public GolemLastSeenSensor() {
        this(200);
    }

    public GolemLastSeenSensor(int i) {
        super(i);
    }

    @Override
    protected void sense(ServerWorld arg, LivingEntity arg2) {
        GolemLastSeenSensor.senseIronGolem(arg2);
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.MOBS);
    }

    public static void senseIronGolem(LivingEntity arg2) {
        Optional<List<LivingEntity>> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.MOBS);
        if (!optional.isPresent()) {
            return;
        }
        boolean bl = optional.get().stream().anyMatch(arg -> arg.getType().equals(EntityType.IRON_GOLEM));
        if (bl) {
            GolemLastSeenSensor.method_30233(arg2);
        }
    }

    public static void method_30233(LivingEntity arg) {
        arg.getBrain().remember(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
    }
}

