/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayWithVillagerBabiesTask
extends Task<MobEntityWithAi> {
    public PlayWithVillagerBabiesTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntityWithAi arg2) {
        return arg.getRandom().nextInt(10) == 0 && this.hasVisibleVillagerBabies(arg2);
    }

    @Override
    protected void run(ServerWorld arg, MobEntityWithAi arg22, long l) {
        LivingEntity lv = this.findVisibleVillagerBaby(arg22);
        if (lv != null) {
            this.setGroundTarget(arg, arg22, lv);
            return;
        }
        Optional<LivingEntity> optional = this.getLeastPopularBabyInteractionTarget(arg22);
        if (optional.isPresent()) {
            PlayWithVillagerBabiesTask.setPlayTarget(arg22, optional.get());
            return;
        }
        this.getVisibleMob(arg22).ifPresent(arg2 -> PlayWithVillagerBabiesTask.setPlayTarget(arg22, arg2));
    }

    private void setGroundTarget(ServerWorld arg, MobEntityWithAi arg2, LivingEntity arg3) {
        for (int i = 0; i < 10; ++i) {
            Vec3d lv = TargetFinder.findGroundTarget(arg2, 20, 8);
            if (lv == null || !arg.isNearOccupiedPointOfInterest(new BlockPos(lv))) continue;
            arg2.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lv, 0.6f, 0));
            return;
        }
    }

    private static void setPlayTarget(MobEntityWithAi arg, LivingEntity arg2) {
        Brain<?> lv = arg.getBrain();
        lv.remember(MemoryModuleType.INTERACTION_TARGET, arg2);
        lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(arg2, true));
        lv.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(arg2, false), 0.6f, 1));
    }

    private Optional<LivingEntity> getVisibleMob(MobEntityWithAi arg) {
        return this.getVisibleVillagerBabies(arg).stream().findAny();
    }

    private Optional<LivingEntity> getLeastPopularBabyInteractionTarget(MobEntityWithAi arg) {
        Map<LivingEntity, Integer> map = this.getBabyInteractionTargetCounts(arg);
        return map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter(entry -> (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5).map(Map.Entry::getKey).findFirst();
    }

    private Map<LivingEntity, Integer> getBabyInteractionTargetCounts(MobEntityWithAi arg) {
        HashMap map = Maps.newHashMap();
        this.getVisibleVillagerBabies(arg).stream().filter(this::hasInteractionTarget).forEach(arg2 -> map.compute(this.getInteractionTarget((LivingEntity)arg2), (arg, integer) -> integer == null ? 1 : integer + 1));
        return map;
    }

    private List<LivingEntity> getVisibleVillagerBabies(MobEntityWithAi arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
    }

    private LivingEntity getInteractionTarget(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }

    @Nullable
    private LivingEntity findVisibleVillagerBaby(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get().stream().filter(arg2 -> this.isInteractionTargetOf(arg, (LivingEntity)arg2)).findAny().orElse(null);
    }

    private boolean hasInteractionTarget(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    private boolean isInteractionTargetOf(LivingEntity arg, LivingEntity arg22) {
        return arg22.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).filter(arg2 -> arg2 == arg).isPresent();
    }

    private boolean hasVisibleVillagerBabies(MobEntityWithAi arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }
}

