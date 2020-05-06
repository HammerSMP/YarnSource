/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.sensor;

import java.util.Random;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
    private static final Random RANDOM = new Random();
    private final int senseInterval;
    private long lastSenseTime;

    public Sensor(int i) {
        this.senseInterval = i;
        this.lastSenseTime = RANDOM.nextInt(i);
    }

    public Sensor() {
        this(20);
    }

    public final void canSense(ServerWorld arg, E arg2) {
        if (--this.lastSenseTime <= 0L) {
            this.lastSenseTime = this.senseInterval;
            this.sense(arg, arg2);
        }
    }

    protected abstract void sense(ServerWorld var1, E var2);

    public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();
}

