/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.AbstractPiglinEntity;
import net.minecraft.PiglinBrute;
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
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.dynamic.GlobalPos;

public class PiglinBruteBrain {
    protected static Brain<?> method_30252(PiglinBrute arg, Brain<PiglinBrute> arg2) {
        PiglinBruteBrain.method_30257(arg, arg2);
        PiglinBruteBrain.method_30260(arg, arg2);
        PiglinBruteBrain.method_30262(arg, arg2);
        arg2.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        arg2.setDefaultActivity(Activity.IDLE);
        arg2.resetPossibleActivities();
        return arg2;
    }

    protected static void method_30250(PiglinBrute arg) {
        GlobalPos lv = GlobalPos.create(arg.world.getRegistryKey(), arg.getBlockPos());
        arg.getBrain().remember(MemoryModuleType.HOME, lv);
    }

    private static void method_30257(PiglinBrute arg, Brain<PiglinBrute> arg2) {
        arg2.setTaskList(Activity.CORE, 0, (ImmutableList<Task<PiglinBrute>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(200), (Object)new OpenDoorsTask(), new ForgetAngryAtTargetTask()));
    }

    private static void method_30260(PiglinBrute arg, Brain<PiglinBrute> arg2) {
        arg2.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<PiglinBrute>>)ImmutableList.of(new UpdateAttackTargetTask<PiglinBrute>(PiglinBruteBrain::method_30247), PiglinBruteBrain.method_30244(), PiglinBruteBrain.method_30254(), (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)));
    }

    private static void method_30262(PiglinBrute arg, Brain<PiglinBrute> arg22) {
        arg22.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<PiglinBrute>>)ImmutableList.of(new ForgetAttackTargetTask(arg2 -> !PiglinBruteBrain.method_30248(arg, arg2)), (Object)new RangedApproachTask(1.0f), (Object)new MeleeAttackTask(20)), MemoryModuleType.ATTACK_TARGET);
    }

    private static RandomTask<PiglinBrute> method_30244() {
        return new RandomTask<PiglinBrute>((List<Pair<Task<PiglinBrute>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN_BRUTE, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(8.0f), (Object)1), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    private static RandomTask<PiglinBrute> method_30254() {
        return new RandomTask<PiglinBrute>((List<Pair<Task<PiglinBrute>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.6f), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of((Object)new GoToNearbyPositionTask(MemoryModuleType.HOME, 0.6f, 2, 100), (Object)2), (Object)Pair.of((Object)new GoToIfNearbyTask(MemoryModuleType.HOME, 0.6f, 5), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    protected static void method_30256(PiglinBrute arg) {
        Brain<PiglinBrute> lv = arg.getBrain();
        Activity lv2 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        lv.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity lv3 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        if (lv2 != lv3) {
            PiglinBruteBrain.method_30261(arg);
        }
        arg.setAttacking(lv.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    private static boolean method_30248(AbstractPiglinEntity arg, LivingEntity arg22) {
        return PiglinBruteBrain.method_30247(arg).filter(arg2 -> arg2 == arg22).isPresent();
    }

    private static Optional<? extends LivingEntity> method_30247(AbstractPiglinEntity arg) {
        Optional<LivingEntity> optional = LookTargetUtil.getEntity(arg, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && PiglinBruteBrain.method_30245(optional.get())) {
            return optional;
        }
        Optional<? extends LivingEntity> optional2 = PiglinBruteBrain.method_30249(arg, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
        if (optional2.isPresent()) {
            return optional2;
        }
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
    }

    private static boolean method_30245(LivingEntity arg) {
        return EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(arg);
    }

    private static Optional<? extends LivingEntity> method_30249(AbstractPiglinEntity arg, MemoryModuleType<? extends LivingEntity> arg22) {
        return arg.getBrain().getOptionalMemory(arg22).filter(arg2 -> arg2.isInRange(arg, 12.0));
    }

    protected static void method_30251(PiglinBrute arg, LivingEntity arg2) {
        if (arg2 instanceof AbstractPiglinEntity) {
            return;
        }
        PiglinBrain.tryRevenge(arg, arg2);
    }

    protected static void method_30258(PiglinBrute arg) {
        if ((double)arg.world.random.nextFloat() < 0.0125) {
            PiglinBruteBrain.method_30261(arg);
        }
    }

    private static void method_30261(PiglinBrute arg) {
        arg.getBrain().getFirstPossibleNonCoreActivity().ifPresent(arg2 -> {
            if (arg2 == Activity.FIGHT) {
                arg.method_30243();
            }
        });
    }
}

