/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;

public class RunAroundAfterRaidTask
extends FindWalkTargetTask {
    public RunAroundAfterRaidTask(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, PathAwareEntity arg2) {
        Raid lv = arg.getRaidAt(arg2.getBlockPos());
        return lv != null && lv.hasWon() && super.shouldRun(arg, arg2);
    }
}

