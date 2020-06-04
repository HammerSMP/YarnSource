/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class ForgetAngryAtTargetTask<E extends MobEntity>
extends Task<E> {
    public ForgetAngryAtTargetTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        LookTargetUtil.getEntity(arg2, MemoryModuleType.ANGRY_AT).ifPresent(arg3 -> {
            if (arg3.method_29504() && (arg3.getType() != EntityType.PLAYER || arg.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))) {
                arg2.getBrain().forget(MemoryModuleType.ANGRY_AT);
            }
        });
    }
}

