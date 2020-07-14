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

    protected static Brain<?> create(Brain<HoglinEntity> brain) {
        HoglinBrain.addCoreTasks(brain);
        HoglinBrain.addIdleTasks(brain);
        HoglinBrain.addFightTasks(brain);
        HoglinBrain.addAvoidTasks(brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreTasks(Brain<HoglinEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(200)));
    }

    private static void addIdleTasks(Brain<HoglinEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of((Object)new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200), (Object)new BreedTask(EntityType.HOGLIN, 0.6f), GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0f, 8, true), new UpdateAttackTargetTask<HoglinEntity>(HoglinBrain::getNearestVisibleTargetablePlayer), new ConditionalTask<PathAwareEntity>(HoglinEntity::isAdult, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4f, 8, false)), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), IntRange.between(30, 60)), new WalkTowardClosestAdultTask(WALK_TOWARD_CLOSEST_ADULT_RANGE, 0.6f), HoglinBrain.makeRandomWalkTask()));
    }

    private static void addFightTasks(Brain<HoglinEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of((Object)new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200), (Object)new BreedTask(EntityType.HOGLIN, 0.6f), (Object)new RangedApproachTask(1.0f), new ConditionalTask<MobEntity>(HoglinEntity::isAdult, new MeleeAttackTask(40)), new ConditionalTask<MobEntity>(PassiveEntity::isBaby, new MeleeAttackTask(15)), new ForgetAttackTargetTask(), new ForgetTask<HoglinEntity>(HoglinBrain::hasBreedTarget, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addAvoidTasks(Brain<HoglinEntity> brain) {
        brain.setTaskList(Activity.AVOID, 10, (ImmutableList<Task<HoglinEntity>>)ImmutableList.of(GoToRememberedPositionTask.toEntity(MemoryModuleType.AVOID_TARGET, 1.3f, 15, false), HoglinBrain.makeRandomWalkTask(), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), IntRange.between(30, 60)), new ForgetTask<HoglinEntity>(HoglinBrain::method_25947, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static RandomTask<HoglinEntity> makeRandomWalkTask() {
        return new RandomTask<HoglinEntity>((List<Pair<Task<HoglinEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.4f), (Object)2), (Object)Pair.of((Object)new GoTowardsLookTarget(0.4f, 3), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    protected static void refreshActivities(HoglinEntity hoglin) {
        Brain<HoglinEntity> lv = hoglin.getBrain();
        Activity lv2 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        lv.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.AVOID, (Object)Activity.IDLE));
        Activity lv3 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        if (lv2 != lv3) {
            HoglinBrain.method_30083(hoglin).ifPresent(hoglin::method_30081);
        }
        hoglin.setAttacking(lv.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    protected static void onAttacking(HoglinEntity hoglin, LivingEntity target) {
        if (hoglin.isBaby()) {
            return;
        }
        if (target.getType() == EntityType.PIGLIN && HoglinBrain.hasMoreHoglinsAround(hoglin)) {
            HoglinBrain.avoid(hoglin, target);
            HoglinBrain.askAdultsToAvoid(hoglin, target);
            return;
        }
        HoglinBrain.askAdultsForHelp(hoglin, target);
    }

    private static void askAdultsToAvoid(HoglinEntity hoglin2, LivingEntity target) {
        HoglinBrain.getAdultHoglinsAround(hoglin2).forEach(hoglin -> HoglinBrain.avoidEnemy(hoglin, target));
    }

    private static void avoidEnemy(HoglinEntity hoglin, LivingEntity target) {
        LivingEntity lv = target;
        Brain<HoglinEntity> lv2 = hoglin.getBrain();
        lv = LookTargetUtil.getCloserEntity((LivingEntity)hoglin, lv2.getOptionalMemory(MemoryModuleType.AVOID_TARGET), lv);
        lv = LookTargetUtil.getCloserEntity((LivingEntity)hoglin, lv2.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), lv);
        HoglinBrain.avoid(hoglin, lv);
    }

    private static void avoid(HoglinEntity hoglin, LivingEntity target) {
        hoglin.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        hoglin.getBrain().forget(MemoryModuleType.WALK_TARGET);
        hoglin.getBrain().remember(MemoryModuleType.AVOID_TARGET, target, AVOID_MEMORY_DURATION.choose(hoglin.world.random));
    }

    private static Optional<? extends LivingEntity> getNearestVisibleTargetablePlayer(HoglinEntity hoglin) {
        if (HoglinBrain.isNearPlayer(hoglin) || HoglinBrain.hasBreedTarget(hoglin)) {
            return Optional.empty();
        }
        return hoglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
    }

    static boolean isWarpedFungusAround(HoglinEntity hoglin, BlockPos pos) {
        Optional<BlockPos> optional = hoglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_REPELLENT);
        return optional.isPresent() && optional.get().isWithinDistance(pos, 8.0);
    }

    private static boolean method_25947(HoglinEntity arg) {
        return arg.isAdult() && !HoglinBrain.hasMoreHoglinsAround(arg);
    }

    private static boolean hasMoreHoglinsAround(HoglinEntity hoglin) {
        int j;
        if (hoglin.isBaby()) {
            return false;
        }
        int i = hoglin.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
        return i > (j = hoglin.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1);
    }

    protected static void onAttacked(HoglinEntity hoglin, LivingEntity attacker) {
        Brain<HoglinEntity> lv = hoglin.getBrain();
        lv.forget(MemoryModuleType.PACIFIED);
        lv.forget(MemoryModuleType.BREED_TARGET);
        if (hoglin.isBaby()) {
            HoglinBrain.avoidEnemy(hoglin, attacker);
            return;
        }
        HoglinBrain.targetEnemy(hoglin, attacker);
    }

    private static void targetEnemy(HoglinEntity hoglin, LivingEntity target) {
        if (hoglin.getBrain().hasActivity(Activity.AVOID) && target.getType() == EntityType.PIGLIN) {
            return;
        }
        if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(target)) {
            return;
        }
        if (target.getType() == EntityType.HOGLIN) {
            return;
        }
        if (LookTargetUtil.isNewTargetTooFar(hoglin, target, 4.0)) {
            return;
        }
        HoglinBrain.setAttackTarget(hoglin, target);
        HoglinBrain.askAdultsForHelp(hoglin, target);
    }

    private static void setAttackTarget(HoglinEntity hoglin, LivingEntity target) {
        Brain<HoglinEntity> lv = hoglin.getBrain();
        lv.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        lv.forget(MemoryModuleType.BREED_TARGET);
        lv.remember(MemoryModuleType.ATTACK_TARGET, target, 200L);
    }

    private static void askAdultsForHelp(HoglinEntity hoglin2, LivingEntity target) {
        HoglinBrain.getAdultHoglinsAround(hoglin2).forEach(hoglin -> HoglinBrain.setAttackTargetIfCloser(hoglin, target));
    }

    private static void setAttackTargetIfCloser(HoglinEntity hoglin, LivingEntity targetCandidate) {
        if (HoglinBrain.isNearPlayer(hoglin)) {
            return;
        }
        Optional<LivingEntity> optional = hoglin.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        LivingEntity lv = LookTargetUtil.getCloserEntity((LivingEntity)hoglin, optional, targetCandidate);
        HoglinBrain.setAttackTarget(hoglin, lv);
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

    private static List<HoglinEntity> getAdultHoglinsAround(HoglinEntity hoglin) {
        return hoglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse((List<HoglinEntity>)ImmutableList.of());
    }

    private static boolean method_30085(HoglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean hasBreedTarget(HoglinEntity hoglin) {
        return hoglin.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
    }

    protected static boolean isNearPlayer(HoglinEntity hoglin) {
        return hoglin.getBrain().hasMemoryModule(MemoryModuleType.PACIFIED);
    }
}

