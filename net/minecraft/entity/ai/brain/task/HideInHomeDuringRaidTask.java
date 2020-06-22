/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.HideInHomeTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;

public class HideInHomeDuringRaidTask
extends HideInHomeTask {
    public HideInHomeDuringRaidTask(int i, float f) {
        super(i, f, 1);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        Raid lv = arg.getRaidAt(arg2.getBlockPos());
        return super.shouldRun(arg, arg2) && lv != null && lv.isActive() && !lv.hasWon() && !lv.hasLost();
    }
}

