/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class GoToWorkTask
extends Task<VillagerEntity> {
    public GoToWorkTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        BlockPos lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        return lv.isWithinDistance(arg2.getPos(), 2.0) || arg2.method_29279();
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        GlobalPos lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
        arg2.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
        arg2.getBrain().remember(MemoryModuleType.JOB_SITE, lv);
        if (arg2.getVillagerData().getProfession() != VillagerProfession.NONE) {
            return;
        }
        MinecraftServer minecraftServer = arg.getServer();
        minecraftServer.getWorld(lv.getDimension()).getPointOfInterestStorage().getType(lv.getPos()).ifPresent(arg32 -> Registry.VILLAGER_PROFESSION.stream().filter(arg2 -> arg2.getWorkStation() == arg32).findFirst().ifPresent(arg3 -> {
            arg2.setVillagerData(arg2.getVillagerData().withProfession((VillagerProfession)arg3));
            arg2.reinitializeBrain(arg);
        }));
    }
}

