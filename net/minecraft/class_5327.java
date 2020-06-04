/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class class_5327
extends Task<VillagerEntity> {
    private final float field_25158;

    public class_5327(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.field_25158 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        if (arg2.isBaby()) {
            return false;
        }
        return arg2.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg22, long l) {
        BlockPos lv = arg22.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        Optional<PointOfInterestType> optional = arg.getPointOfInterestStorage().getType(lv);
        if (!optional.isPresent()) {
            return;
        }
        LookTargetUtil.method_29248(arg22, arg2 -> this.method_29260((PointOfInterestType)optional.get(), (VillagerEntity)arg2, lv)).findFirst().ifPresent(arg4 -> this.method_29266(arg, arg22, (VillagerEntity)arg4, lv, arg4.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent()));
    }

    private boolean method_29260(PointOfInterestType arg, VillagerEntity arg2, BlockPos arg3) {
        boolean bl = arg2.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
        if (bl) {
            return false;
        }
        Optional<GlobalPos> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        VillagerProfession lv = arg2.getVillagerData().getProfession();
        if (arg2.getVillagerData().getProfession() != VillagerProfession.NONE && lv.getWorkStation().getCompletionCondition().test(arg)) {
            if (!optional.isPresent()) {
                return this.method_29262(arg2, arg3, arg);
            }
            return optional.get().getPos().equals(arg3);
        }
        return false;
    }

    private void method_29266(ServerWorld arg, VillagerEntity arg2, VillagerEntity arg3, BlockPos arg4, boolean bl) {
        this.method_29261(arg2);
        if (!bl) {
            LookTargetUtil.walkTowards((LivingEntity)arg3, arg4, this.field_25158, 1);
            arg3.getBrain().remember(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.create(arg.getRegistryKey(), arg4));
            DebugInfoSender.sendPointOfInterest(arg, arg4);
        }
    }

    private boolean method_29262(VillagerEntity arg, BlockPos arg2, PointOfInterestType arg3) {
        Path lv = arg.getNavigation().findPathTo(arg2, arg3.getSearchDistance());
        return lv != null && lv.reachesTarget();
    }

    private void method_29261(VillagerEntity arg) {
        arg.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        arg.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

