/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;

public class SeekSkyAfterRaidWinTask
extends SeekSkyTask {
    public SeekSkyAfterRaidWinTask(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        Raid lv = arg.getRaidAt(arg2.getBlockPos());
        return lv != null && lv.hasWon() && super.shouldRun(arg, arg2);
    }
}

