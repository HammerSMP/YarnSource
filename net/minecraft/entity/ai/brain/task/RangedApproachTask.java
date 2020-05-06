/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class RangedApproachTask
extends Task<MobEntity> {
    private final float speed;

    public RangedApproachTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.speed = f;
    }

    @Override
    protected void run(ServerWorld arg, MobEntity arg2, long l) {
        LivingEntity lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (LookTargetUtil.isVisibleInMemory(arg2, lv) && LookTargetUtil.method_25940(arg2, lv, 1)) {
            this.forgetWalkTarget(arg2);
        } else {
            this.rememberWalkTarget(arg2, lv);
        }
    }

    private void rememberWalkTarget(LivingEntity arg, LivingEntity arg2) {
        Brain<?> lv = arg.getBrain();
        lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(arg2, true));
        WalkTarget lv2 = new WalkTarget(new EntityLookTarget(arg2, false), this.speed, 0);
        lv.remember(MemoryModuleType.WALK_TARGET, lv2);
    }

    private void forgetWalkTarget(LivingEntity arg) {
        arg.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }
}

