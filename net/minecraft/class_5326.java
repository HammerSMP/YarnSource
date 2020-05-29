/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class class_5326
extends Task<VillagerEntity> {
    final VillagerProfession field_25156;

    public class_5326(VillagerProfession arg) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.field_25156 = arg;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        GlobalPos lv = arg2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        arg.getPointOfInterestStorage().getType(lv.getPos()).ifPresent(arg32 -> LookTargetUtil.method_29248(arg2, arg3 -> this.method_29257(lv, (PointOfInterestType)arg32, (VillagerEntity)arg3)).reduce(arg2, class_5326::method_29255));
    }

    private static VillagerEntity method_29255(VillagerEntity arg, VillagerEntity arg2) {
        VillagerEntity lv4;
        VillagerEntity lv3;
        if (arg.getExperience() > arg2.getExperience()) {
            VillagerEntity lv = arg;
            VillagerEntity lv2 = arg2;
        } else {
            lv3 = arg2;
            lv4 = arg;
        }
        lv4.getBrain().forget(MemoryModuleType.JOB_SITE);
        return lv3;
    }

    private boolean method_29257(GlobalPos arg, PointOfInterestType arg2, VillagerEntity arg3) {
        return this.method_29254(arg3) && arg.equals(arg3.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get()) && this.method_29253(arg2, arg3.getVillagerData().getProfession());
    }

    private boolean method_29253(PointOfInterestType arg, VillagerProfession arg2) {
        return arg2.getWorkStation().getCompletionCondition().test(arg);
    }

    private boolean method_29254(VillagerEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}

