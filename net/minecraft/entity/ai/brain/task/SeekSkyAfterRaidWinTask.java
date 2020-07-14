/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;

public class SeekSkyAfterRaidWinTask
extends SeekSkyTask {
    public SeekSkyAfterRaidWinTask(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        Raid lv = world.getRaidAt(entity.getBlockPos());
        return lv != null && lv.hasWon() && super.shouldRun(world, entity);
    }
}

