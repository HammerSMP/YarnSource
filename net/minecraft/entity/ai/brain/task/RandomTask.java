/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.Task;

public class RandomTask<E extends LivingEntity>
extends CompositeTask<E> {
    public RandomTask(List<Pair<Task<? super E>, Integer>> list) {
        this((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(), list);
    }

    public RandomTask(Map<MemoryModuleType<?>, MemoryModuleState> map, List<Pair<Task<? super E>, Integer>> list) {
        super(map, (Set<MemoryModuleType<?>>)ImmutableSet.of(), CompositeTask.Order.SHUFFLED, CompositeTask.RunMode.RUN_ONE, list);
    }
}

