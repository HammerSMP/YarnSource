/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
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
        return lv.isWithinDistance(arg2.getPos(), 2.0) || arg2.isNatural();
    }

    @Override
    protected void run(ServerWorld arg4, VillagerEntity arg22, long l) {
        GlobalPos lv = arg22.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
        arg22.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
        arg22.getBrain().remember(MemoryModuleType.JOB_SITE, lv);
        if (arg22.getVillagerData().getProfession() != VillagerProfession.NONE) {
            return;
        }
        MinecraftServer minecraftServer = arg4.getServer();
        Optional.ofNullable(minecraftServer.getWorld(lv.getDimension())).flatMap(arg2 -> arg2.getPointOfInterestStorage().getType(lv.getPos())).flatMap(arg -> Registry.VILLAGER_PROFESSION.stream().filter(arg2 -> arg2.getWorkStation() == arg).findFirst()).ifPresent(arg3 -> {
            arg22.setVillagerData(arg22.getVillagerData().withProfession((VillagerProfession)arg3));
            arg22.reinitializeBrain(arg4);
        });
    }
}

