/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.dynamic.GlobalPos;

public class PiglinBruteBrain {
    protected static Brain<?> create(PiglinBruteEntity arg, Brain<PiglinBruteEntity> arg2) {
        PiglinBruteBrain.initGeneralTasks(arg, arg2);
        PiglinBruteBrain.method_30260(arg, arg2);
        PiglinBruteBrain.method_30262(arg, arg2);
        arg2.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        arg2.setDefaultActivity(Activity.IDLE);
        arg2.resetPossibleActivities();
        return arg2;
    }

    protected static void method_30250(PiglinBruteEntity arg) {
        GlobalPos lv = GlobalPos.create(arg.world.getRegistryKey(), arg.getBlockPos());
        arg.getBrain().remember(MemoryModuleType.HOME, lv);
    }

    private static void initGeneralTasks(PiglinBruteEntity arg, Brain<PiglinBruteEntity> arg2) {
        arg2.setTaskList(Activity.CORE, 0, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(200), (Object)new OpenDoorsTask(), new ForgetAngryAtTargetTask()));
    }

    private static void method_30260(PiglinBruteEntity arg, Brain<PiglinBruteEntity> arg2) {
        arg2.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of(new UpdateAttackTargetTask<PiglinBruteEntity>(PiglinBruteBrain::method_30247), PiglinBruteBrain.method_30244(), PiglinBruteBrain.method_30254(), (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)));
    }

    private static void method_30262(PiglinBruteEntity arg, Brain<PiglinBruteEntity> arg22) {
        arg22.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of(new ForgetAttackTargetTask(arg2 -> !PiglinBruteBrain.method_30248(arg, arg2)), (Object)new RangedApproachTask(1.0f), (Object)new MeleeAttackTask(20)), MemoryModuleType.ATTACK_TARGET);
    }

    private static RandomTask<PiglinBruteEntity> method_30244() {
        return new RandomTask<PiglinBruteEntity>((List<Pair<Task<PiglinBruteEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN_BRUTE, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(8.0f), (Object)1), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    private static RandomTask<PiglinBruteEntity> method_30254() {
        return new RandomTask<PiglinBruteEntity>((List<Pair<Task<PiglinBruteEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.6f), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of((Object)new GoToNearbyPositionTask(MemoryModuleType.HOME, 0.6f, 2, 100), (Object)2), (Object)Pair.of((Object)new GoToIfNearbyTask(MemoryModuleType.HOME, 0.6f, 5), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    protected static void method_30256(PiglinBruteEntity arg) {
        Brain<PiglinBruteEntity> lv = arg.getBrain();
        Activity lv2 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        lv.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity lv3 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        if (lv2 != lv3) {
            PiglinBruteBrain.playAngerSoundIfAngry(arg);
        }
        arg.setAttacking(lv.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    private static boolean method_30248(AbstractPiglinEntity arg, LivingEntity arg22) {
        return PiglinBruteBrain.method_30247(arg).filter(arg2 -> arg2 == arg22).isPresent();
    }

    private static Optional<? extends LivingEntity> method_30247(AbstractPiglinEntity Piglin) {
        Optional<LivingEntity> angerTargetMemory = LookTargetUtil.getEntity(Piglin, MemoryModuleType.ANGRY_AT);
        if (angerTargetMemory.isPresent() && PiglinBruteBrain.isPlayerAttackable(angerTargetMemory.get())) {
            return angerTargetMemory;
        }
        Optional<? extends LivingEntity> nearbyPlayerMemory = PiglinBruteBrain.checkMemoryEntityRange(Piglin, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
        if (nearbyPlayerMemory.isPresent()) {
            return nearbyPlayerMemory;
        }
        return Piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
    }

    private static boolean isPlayerAttackable(LivingEntity player) {
        return EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(player);
    }

    private static Optional<? extends LivingEntity> checkMemoryEntityRange(AbstractPiglinEntity Piglin, MemoryModuleType<? extends LivingEntity> LivingEntityMemory) {
        return Piglin.getBrain().getOptionalMemory(LivingEntityMemory).filter(LivingEntity -> LivingEntity.isInRange(Piglin, 12.0));
    }

    protected static void angerNearbyPiglins(PiglinBruteEntity arg, LivingEntity arg2) {
        if (arg2 instanceof AbstractPiglinEntity) {
            return;
        }
        PiglinBrain.tryRevenge(arg, arg2);
    }

    protected static void tryAngerSound(PiglinBruteEntity arg) {
        if ((double)arg.world.random.nextFloat() < 0.0125) {
            PiglinBruteBrain.playAngerSoundIfAngry(arg);
        }
    }

    private static void playAngerSoundIfAngry(PiglinBruteEntity arg) {
        arg.getBrain().getFirstPossibleNonCoreActivity().ifPresent(arg2 -> {
            if (arg2 == Activity.FIGHT) {
                arg.playAngrySound();
            }
        });
    }
}

