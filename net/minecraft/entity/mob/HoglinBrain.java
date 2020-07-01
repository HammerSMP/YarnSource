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
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.PacifyTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;

public class HoglinBrain {
    private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);
    private static final IntRange WALK_TOWARD_CLOSEST_ADULT_RANGE = IntRange.between(5, 16);

    protected static Brain<?> create(Brain<HoglinEntity> arg) {
        HoglinBrain.addCoreTasks(arg);
        HoglinBrain.addIdleTasks(arg);
        HoglinBrain.addFightTasks(arg);
        HoglinBrain.addAvoidTasks(arg);
        arg.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        arg.setDefaultActivity(Activity.IDLE);
        arg.resetPossibleActivities();
        return arg;
    }

    private static void addCoreTasks(Brain<HoglinEntity> arg) {
        arg.setTaskList(Activity.CORE, 0, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(200)));
    }

    private static void addIdleTasks(Brain<HoglinEntity> arg) {
        arg.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of((Object)new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200), (Object)new BreedTask(EntityType.HOGLIN, 0.6f), GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0f, 8, true), new UpdateAttackTargetTask<HoglinEntity>(HoglinBrain::getNearestVisibleTargetablePlayer), new ConditionalTask<PathAwareEntity>(HoglinEntity::isAdult, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4f, 8, false)), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), IntRange.between(30, 60)), new WalkTowardClosestAdultTask(WALK_TOWARD_CLOSEST_ADULT_RANGE, 0.6f), HoglinBrain.makeRandomWalkTask()));
    }

    private static void addFightTasks(Brain<HoglinEntity> arg) {
        arg.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of((Object)new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200), (Object)new BreedTask(EntityType.HOGLIN, 0.6f), (Object)new RangedApproachTask(1.0f), new ConditionalTask<MobEntity>(HoglinEntity::isAdult, new MeleeAttackTask(40)), new ConditionalTask<MobEntity>(PassiveEntity::isBaby, new MeleeAttackTask(15)), new ForgetAttackTargetTask(), new ForgetTask<HoglinEntity>(HoglinBrain::hasBreedTarget, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addAvoidTasks(Brain<HoglinEntity> arg) {
        arg.setTaskList(Activity.AVOID, 10, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of(GoToRememberedPositionTask.toEntity(MemoryModuleType.AVOID_TARGET, 1.3f, 15, false), HoglinBrain.makeRandomWalkTask(), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), IntRange.between(30, 60)), new ForgetTask<HoglinEntity>(HoglinBrain::method_25947, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static RandomTask<HoglinEntity> makeRandomWalkTask() {
        return new RandomTask<HoglinEntity>((List<Pair<Task<HoglinEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.4f), (Object)2), (Object)Pair.of((Object)new GoTowardsLookTarget(0.4f, 3), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    protected static void refreshActivities(HoglinEntity arg) {
        Brain<HoglinEntity> lv = arg.getBrain();
        Activity lv2 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        lv.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.AVOID, (Object)Activity.IDLE));
        Activity lv3 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        if (lv2 != lv3) {
            HoglinBrain.method_30083(arg).ifPresent(arg::method_30081);
        }
        arg.setAttacking(lv.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    protected static void onAttacking(HoglinEntity arg, LivingEntity arg2) {
        if (arg.isBaby()) {
            return;
        }
        if (arg2.getType() == EntityType.PIGLIN && HoglinBrain.hasMoreHoglinsAround(arg)) {
            HoglinBrain.avoid(arg, arg2);
            HoglinBrain.askAdultsToAvoid(arg, arg2);
            return;
        }
        HoglinBrain.askAdultsForHelp(arg, arg2);
    }

    private static void askAdultsToAvoid(HoglinEntity arg, LivingEntity arg22) {
        HoglinBrain.getAdultHoglinsAround(arg).forEach(arg2 -> HoglinBrain.avoidEnemy(arg2, arg22));
    }

    private static void avoidEnemy(HoglinEntity arg, LivingEntity arg2) {
        LivingEntity lv = arg2;
        Brain<HoglinEntity> lv2 = arg.getBrain();
        lv = LookTargetUtil.getCloserEntity((LivingEntity)arg, lv2.getOptionalMemory(MemoryModuleType.AVOID_TARGET), lv);
        lv = LookTargetUtil.getCloserEntity((LivingEntity)arg, lv2.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), lv);
        HoglinBrain.avoid(arg, lv);
    }

    private static void avoid(HoglinEntity arg, LivingEntity arg2) {
        arg.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        arg.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg.getBrain().remember(MemoryModuleType.AVOID_TARGET, arg2, AVOID_MEMORY_DURATION.choose(arg.world.random));
    }

    private static Optional<? extends LivingEntity> getNearestVisibleTargetablePlayer(HoglinEntity arg) {
        if (HoglinBrain.isNearPlayer(arg) || HoglinBrain.hasBreedTarget(arg)) {
            return Optional.empty();
        }
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
    }

    static boolean isWarpedFungusAround(HoglinEntity arg, BlockPos arg2) {
        Optional<BlockPos> optional = arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_REPELLENT);
        return optional.isPresent() && optional.get().isWithinDistance(arg2, 8.0);
    }

    private static boolean method_25947(HoglinEntity arg) {
        return arg.isAdult() && !HoglinBrain.hasMoreHoglinsAround(arg);
    }

    private static boolean hasMoreHoglinsAround(HoglinEntity arg) {
        int j;
        if (arg.isBaby()) {
            return false;
        }
        int i = arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
        return i > (j = arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1);
    }

    protected static void onAttacked(HoglinEntity arg, LivingEntity arg2) {
        Brain<HoglinEntity> lv = arg.getBrain();
        lv.forget(MemoryModuleType.PACIFIED);
        lv.forget(MemoryModuleType.BREED_TARGET);
        if (arg.isBaby()) {
            HoglinBrain.avoidEnemy(arg, arg2);
            return;
        }
        HoglinBrain.targetEnemy(arg, arg2);
    }

    private static void targetEnemy(HoglinEntity arg, LivingEntity arg2) {
        if (arg.getBrain().hasActivity(Activity.AVOID) && arg2.getType() == EntityType.PIGLIN) {
            return;
        }
        if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(arg2)) {
            return;
        }
        if (arg2.getType() == EntityType.HOGLIN) {
            return;
        }
        if (LookTargetUtil.isNewTargetTooFar(arg, arg2, 4.0)) {
            return;
        }
        HoglinBrain.setAttackTarget(arg, arg2);
        HoglinBrain.askAdultsForHelp(arg, arg2);
    }

    private static void setAttackTarget(HoglinEntity arg, LivingEntity arg2) {
        Brain<HoglinEntity> lv = arg.getBrain();
        lv.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        lv.forget(MemoryModuleType.BREED_TARGET);
        lv.remember(MemoryModuleType.ATTACK_TARGET, arg2, 200L);
    }

    private static void askAdultsForHelp(HoglinEntity arg, LivingEntity arg22) {
        HoglinBrain.getAdultHoglinsAround(arg).forEach(arg2 -> HoglinBrain.setAttackTargetIfCloser(arg2, arg22));
    }

    private static void setAttackTargetIfCloser(HoglinEntity arg, LivingEntity arg2) {
        if (HoglinBrain.isNearPlayer(arg)) {
            return;
        }
        Optional<LivingEntity> optional = arg.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        LivingEntity lv = LookTargetUtil.getCloserEntity((LivingEntity)arg, optional, arg2);
        HoglinBrain.setAttackTarget(arg, lv);
    }

    public static Optional<SoundEvent> method_30083(HoglinEntity arg) {
        return arg.getBrain().getFirstPossibleNonCoreActivity().map(arg2 -> HoglinBrain.method_30082(arg, arg2));
    }

    private static SoundEvent method_30082(HoglinEntity arg, Activity arg2) {
        if (arg2 == Activity.AVOID || arg.canConvert()) {
            return SoundEvents.ENTITY_HOGLIN_RETREAT;
        }
        if (arg2 == Activity.FIGHT) {
            return SoundEvents.ENTITY_HOGLIN_ANGRY;
        }
        if (HoglinBrain.method_30085(arg)) {
            return SoundEvents.ENTITY_HOGLIN_RETREAT;
        }
        return SoundEvents.ENTITY_HOGLIN_AMBIENT;
    }

    private static List<HoglinEntity> getAdultHoglinsAround(HoglinEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse((List<HoglinEntity>)ImmutableList.of());
    }

    private static boolean method_30085(HoglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean hasBreedTarget(HoglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
    }

    protected static boolean isNearPlayer(HoglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.PACIFIED);
    }
}

