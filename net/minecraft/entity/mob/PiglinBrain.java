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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.AbstractPiglinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AdmireItemTask;
import net.minecraft.entity.ai.brain.task.AttackTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import net.minecraft.entity.ai.brain.task.DefeatTargetTask;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoToCelebrateTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.HuntFinishTask;
import net.minecraft.entity.ai.brain.task.HuntHoglinTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.MemoryTransferTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.RemoveOffHandItemTask;
import net.minecraft.entity.ai.brain.task.RidingTask;
import net.minecraft.entity.ai.brain.task.StartRidingTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.brain.task.WantNewItemTask;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

public class PiglinBrain {
    public static final Item BARTERING_ITEM = Items.GOLD_INGOT;
    private static final IntRange HUNT_MEMORY_DURATION = Durations.betweenSeconds(30, 120);
    private static final IntRange MEMORY_TRANSFER_TASK_DURATION = Durations.betweenSeconds(10, 40);
    private static final IntRange RIDE_TARGET_MEMORY_DURATION = Durations.betweenSeconds(10, 30);
    private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);
    private static final IntRange field_25384 = Durations.betweenSeconds(5, 7);
    private static final IntRange field_25698 = Durations.betweenSeconds(5, 7);
    private static final Set<Item> FOOD = ImmutableSet.of((Object)Items.PORKCHOP, (Object)Items.COOKED_PORKCHOP);

    protected static Brain<?> create(PiglinEntity arg, Brain<PiglinEntity> arg2) {
        PiglinBrain.addCoreActivities(arg2);
        PiglinBrain.addIdleActivities(arg2);
        PiglinBrain.addAdmireItemActivities(arg2);
        PiglinBrain.addFightActivities(arg, arg2);
        PiglinBrain.addCelebrateActivities(arg2);
        PiglinBrain.addAvoidActivities(arg2);
        PiglinBrain.addRideActivities(arg2);
        arg2.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        arg2.setDefaultActivity(Activity.IDLE);
        arg2.resetPossibleActivities();
        return arg2;
    }

    protected static void setHuntedRecently(PiglinEntity arg) {
        int i = HUNT_MEMORY_DURATION.choose(arg.world.random);
        arg.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, true, i);
    }

    private static void addCoreActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.CORE, 0, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(200), (Object)new OpenDoorsTask(), PiglinBrain.method_30090(), PiglinBrain.makeGoToZombifiedPiglinTask(), new RemoveOffHandItemTask(), new AdmireItemTask(120), (Object)new DefeatTargetTask(300, PiglinBrain::method_29276), new ForgetAngryAtTargetTask()));
    }

    private static void addIdleActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of((Object)new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0f), new UpdateAttackTargetTask<PiglinEntity>(AbstractPiglinEntity::method_30236, PiglinBrain::getPreferredTarget), new ConditionalTask<PiglinEntity>(PiglinEntity::canHunt, new HuntHoglinTask()), PiglinBrain.makeGoToSoulFireTask(), PiglinBrain.makeRememberRideableHoglinTask(), PiglinBrain.makeRandomFollowTask(), PiglinBrain.makeRandomWanderTask(), (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)));
    }

    private static void addFightActivities(PiglinEntity arg, Brain<PiglinEntity> arg22) {
        arg22.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(new ForgetAttackTargetTask(arg2 -> !PiglinBrain.isPreferredAttackTarget(arg, arg2)), new ConditionalTask<PiglinEntity>(PiglinBrain::isHoldingCrossbow, new AttackTask(5, 0.75f)), (Object)new RangedApproachTask(1.0f), (Object)new MeleeAttackTask(20), new CrossbowAttackTask(), new HuntFinishTask(), new ForgetTask<PiglinEntity>(PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addCelebrateActivities(Brain<PiglinEntity> arg2) {
        arg2.setTaskList(Activity.CELEBRATE, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(PiglinBrain.makeGoToSoulFireTask(), (Object)new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0f), new UpdateAttackTargetTask<PiglinEntity>(AbstractPiglinEntity::method_30236, PiglinBrain::getPreferredTarget), new ConditionalTask<PiglinEntity>(arg -> !arg.isDancing(), new GoToCelebrateTask(2, 1.0f)), new ConditionalTask<PiglinEntity>(PiglinEntity::isDancing, new GoToCelebrateTask(4, 0.6f)), new RandomTask(ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of((Object)new StrollTask(0.6f, 2, 1), (Object)1), (Object)Pair.of((Object)new WaitTask(10, 20), (Object)1)))), MemoryModuleType.CELEBRATE_LOCATION);
    }

    private static void addAdmireItemActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.ADMIRE_ITEM, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(new WalkToNearestVisibleWantedItemTask<PiglinEntity>(PiglinBrain::doesNotHaveGoldInOffHand, 1.0f, true, 9), new WantNewItemTask(9)), MemoryModuleType.ADMIRING_ITEM);
    }

    private static void addAvoidActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.AVOID, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(GoToRememberedPositionTask.toEntity(MemoryModuleType.AVOID_TARGET, 1.0f, 12, true), PiglinBrain.makeRandomFollowTask(), PiglinBrain.makeRandomWanderTask(), new ForgetTask<PiglinEntity>(PiglinBrain::shouldRunAwayFromHoglins, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static void addRideActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.RIDE, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(new StartRidingTask(0.8f), (Object)new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 8.0f), new ConditionalTask<PiglinEntity>(Entity::hasVehicle, PiglinBrain.makeRandomFollowTask()), new RidingTask(8, PiglinBrain::canRide)), MemoryModuleType.RIDE_TARGET);
    }

    private static RandomTask<PiglinEntity> makeRandomFollowTask() {
        return new RandomTask<PiglinEntity>((List<Pair<Task<PiglinEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(8.0f), (Object)1), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    private static RandomTask<PiglinEntity> makeRandomWanderTask() {
        return new RandomTask<PiglinEntity>((List<Pair<Task<PiglinEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.6f), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(new ConditionalTask<LivingEntity>(PiglinBrain::canWander, new GoTowardsLookTarget(0.6f, 3)), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    private static GoToRememberedPositionTask<BlockPos> makeGoToSoulFireTask() {
        return GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0f, 8, false);
    }

    private static MemoryTransferTask<PiglinEntity, LivingEntity> method_30090() {
        return new MemoryTransferTask<PiglinEntity, LivingEntity>(PiglinEntity::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, field_25698);
    }

    private static MemoryTransferTask<PiglinEntity, LivingEntity> makeGoToZombifiedPiglinTask() {
        return new MemoryTransferTask<PiglinEntity, LivingEntity>(PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, field_25384);
    }

    protected static void tickActivities(PiglinEntity arg) {
        Brain<PiglinEntity> lv = arg.getBrain();
        Activity lv2 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        lv.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.ADMIRE_ITEM, (Object)Activity.FIGHT, (Object)Activity.AVOID, (Object)Activity.CELEBRATE, (Object)Activity.RIDE, (Object)Activity.IDLE));
        Activity lv3 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        if (lv2 != lv3) {
            PiglinBrain.method_30091(arg).ifPresent(arg::method_30086);
        }
        arg.setAttacking(lv.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
        if (!lv.hasMemoryModule(MemoryModuleType.RIDE_TARGET) && PiglinBrain.canRideHoglin(arg)) {
            arg.stopRiding();
        }
        if (!lv.hasMemoryModule(MemoryModuleType.CELEBRATE_LOCATION)) {
            lv.forget(MemoryModuleType.DANCING);
        }
        arg.setDancing(lv.hasMemoryModule(MemoryModuleType.DANCING));
    }

    private static boolean canRideHoglin(PiglinEntity arg) {
        if (!arg.isBaby()) {
            return false;
        }
        Entity lv = arg.getVehicle();
        return lv instanceof PiglinEntity && ((PiglinEntity)lv).isBaby() || lv instanceof HoglinEntity && ((HoglinEntity)lv).isBaby();
    }

    protected static void loot(PiglinEntity arg, ItemEntity arg2) {
        ItemStack lv2;
        PiglinBrain.stopWalking(arg);
        if (arg2.getStack().getItem() == Items.GOLD_NUGGET) {
            arg.sendPickup(arg2, arg2.getStack().getCount());
            ItemStack lv = arg2.getStack();
            arg2.remove();
        } else {
            arg.sendPickup(arg2, 1);
            lv2 = PiglinBrain.getItemFromStack(arg2);
        }
        Item lv3 = lv2.getItem();
        if (PiglinBrain.isGoldenItem(lv3)) {
            PiglinBrain.swapItemWithOffHand(arg, lv2);
            PiglinBrain.setAdmiringItem(arg);
            return;
        }
        if (PiglinBrain.isFood(lv3) && !PiglinBrain.hasAteRecently(arg)) {
            PiglinBrain.setEatenRecently(arg);
            return;
        }
        boolean bl = arg.tryEquip(lv2);
        if (bl) {
            return;
        }
        PiglinBrain.barterItem(arg, lv2);
    }

    private static void swapItemWithOffHand(PiglinEntity arg, ItemStack arg2) {
        if (PiglinBrain.hasItemInOffHand(arg)) {
            arg.dropStack(arg.getStackInHand(Hand.OFF_HAND));
        }
        arg.equipToOffHand(arg2);
    }

    private static ItemStack getItemFromStack(ItemEntity arg) {
        ItemStack lv = arg.getStack();
        ItemStack lv2 = lv.split(1);
        if (lv.isEmpty()) {
            arg.remove();
        } else {
            arg.setStack(lv);
        }
        return lv2;
    }

    protected static void consumeOffHandItem(PiglinEntity arg, boolean bl) {
        ItemStack lv = arg.getStackInHand(Hand.OFF_HAND);
        arg.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        if (arg.method_30236()) {
            boolean bl3;
            boolean bl2 = PiglinBrain.acceptsForBarter(lv.getItem());
            if (bl && bl2) {
                PiglinBrain.doBarter(arg, PiglinBrain.getBarteredItem(arg));
            } else if (!bl2 && !(bl3 = arg.tryEquip(lv))) {
                PiglinBrain.barterItem(arg, lv);
            }
        } else {
            boolean bl4 = arg.tryEquip(lv);
            if (!bl4) {
                ItemStack lv2 = arg.getMainHandStack();
                if (PiglinBrain.isGoldenItem(lv2.getItem())) {
                    PiglinBrain.barterItem(arg, lv2);
                } else {
                    PiglinBrain.doBarter(arg, Collections.singletonList(lv2));
                }
                arg.equipToMainHand(lv);
            }
        }
    }

    protected static void pickupItemWithOffHand(PiglinEntity arg) {
        if (PiglinBrain.isAdmiringItem(arg) && !arg.getOffHandStack().isEmpty()) {
            arg.dropStack(arg.getOffHandStack());
            arg.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        }
    }

    private static void barterItem(PiglinEntity arg, ItemStack arg2) {
        ItemStack lv = arg.addItem(arg2);
        PiglinBrain.dropBarteredItem(arg, Collections.singletonList(lv));
    }

    private static void doBarter(PiglinEntity arg, List<ItemStack> list) {
        Optional<PlayerEntity> optional = arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        if (optional.isPresent()) {
            PiglinBrain.dropBarteredItem(arg, optional.get(), list);
        } else {
            PiglinBrain.dropBarteredItem(arg, list);
        }
    }

    private static void dropBarteredItem(PiglinEntity arg, List<ItemStack> list) {
        PiglinBrain.drop(arg, list, PiglinBrain.findGround(arg));
    }

    private static void dropBarteredItem(PiglinEntity arg, PlayerEntity arg2, List<ItemStack> list) {
        PiglinBrain.drop(arg, list, arg2.getPos());
    }

    private static void drop(PiglinEntity arg, List<ItemStack> list, Vec3d arg2) {
        if (!list.isEmpty()) {
            arg.swingHand(Hand.OFF_HAND);
            for (ItemStack lv : list) {
                LookTargetUtil.give(arg, lv, arg2.add(0.0, 1.0, 0.0));
            }
        }
    }

    private static List<ItemStack> getBarteredItem(PiglinEntity arg) {
        LootTable lv = arg.world.getServer().getLootManager().getTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);
        List<ItemStack> list = lv.generateLoot(new LootContext.Builder((ServerWorld)arg.world).parameter(LootContextParameters.THIS_ENTITY, arg).random(arg.world.random).build(LootContextTypes.BARTER));
        return list;
    }

    private static boolean method_29276(LivingEntity arg, LivingEntity arg2) {
        if (arg2.getType() != EntityType.HOGLIN) {
            return false;
        }
        return new Random(arg.world.getTime()).nextFloat() < 0.1f;
    }

    protected static boolean canGather(PiglinEntity arg, ItemStack arg2) {
        Item lv = arg2.getItem();
        if (lv.isIn(ItemTags.PIGLIN_REPELLENTS)) {
            return false;
        }
        if (PiglinBrain.hasBeenHitByPlayer(arg) && arg.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
            return false;
        }
        if (PiglinBrain.acceptsForBarter(lv)) {
            return PiglinBrain.doesNotHaveGoldInOffHand(arg);
        }
        boolean bl = arg.canInsertIntoInventory(arg2);
        if (lv == Items.GOLD_NUGGET) {
            return bl;
        }
        if (PiglinBrain.isFood(lv)) {
            return !PiglinBrain.hasAteRecently(arg) && bl;
        }
        if (PiglinBrain.isGoldenItem(lv)) {
            return PiglinBrain.doesNotHaveGoldInOffHand(arg) && bl;
        }
        return arg.method_24846(arg2);
    }

    protected static boolean isGoldenItem(Item arg) {
        return arg.isIn(ItemTags.PIGLIN_LOVED);
    }

    private static boolean canRide(PiglinEntity arg, Entity arg2) {
        if (arg2 instanceof MobEntity) {
            MobEntity lv = (MobEntity)arg2;
            return !lv.isBaby() || !lv.isAlive() || PiglinBrain.hasBeenHurt(arg) || PiglinBrain.hasBeenHurt(lv) || lv instanceof PiglinEntity && lv.getVehicle() == null;
        }
        return false;
    }

    private static boolean isPreferredAttackTarget(PiglinEntity arg, LivingEntity arg22) {
        return PiglinBrain.getPreferredTarget(arg).filter(arg2 -> arg2 == arg22).isPresent();
    }

    private static boolean getNearestZombifiedPiglin(PiglinEntity arg) {
        Brain<PiglinEntity> lv = arg.getBrain();
        if (lv.hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
            LivingEntity lv2 = lv.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
            return arg.isInRange(lv2, 6.0);
        }
        return false;
    }

    private static Optional<? extends LivingEntity> getPreferredTarget(PiglinEntity arg) {
        Optional<PlayerEntity> optional2;
        Brain<PiglinEntity> lv = arg.getBrain();
        if (PiglinBrain.getNearestZombifiedPiglin(arg)) {
            return Optional.empty();
        }
        Optional<LivingEntity> optional = LookTargetUtil.getEntity(arg, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && PiglinBrain.shouldAttack(optional.get())) {
            return optional;
        }
        if (lv.hasMemoryModule(MemoryModuleType.UNIVERSAL_ANGER) && (optional2 = lv.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)).isPresent()) {
            return optional2;
        }
        Optional<MobEntity> optional3 = lv.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
        if (optional3.isPresent()) {
            return optional3;
        }
        Optional<PlayerEntity> optional4 = lv.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
        if (optional4.isPresent() && PiglinBrain.shouldAttack(optional4.get())) {
            return optional4;
        }
        return Optional.empty();
    }

    public static void onGuardedBlockBroken(PlayerEntity arg, boolean bl) {
        List<PiglinEntity> list = arg.world.getNonSpectatingEntities(PiglinEntity.class, arg.getBoundingBox().expand(16.0));
        list.stream().filter(PiglinBrain::hasIdleActivity).filter(arg2 -> !bl || LookTargetUtil.isVisibleInMemory(arg2, arg)).forEach(arg2 -> {
            if (arg2.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
                PiglinBrain.becomeAngryWithPlayer(arg2, arg);
            } else {
                PiglinBrain.becomeAngryWith(arg2, arg);
            }
        });
    }

    public static ActionResult playerInteract(PiglinEntity arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        if (PiglinBrain.isWillingToTrade(arg, lv)) {
            ItemStack lv2 = lv.split(1);
            PiglinBrain.swapItemWithOffHand(arg, lv2);
            PiglinBrain.setAdmiringItem(arg);
            PiglinBrain.stopWalking(arg);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    protected static boolean isWillingToTrade(PiglinEntity arg, ItemStack arg2) {
        return !PiglinBrain.hasBeenHitByPlayer(arg) && !PiglinBrain.isAdmiringItem(arg) && arg.method_30236() && PiglinBrain.acceptsForBarter(arg2.getItem());
    }

    protected static void onAttacked(PiglinEntity arg, LivingEntity arg2) {
        if (arg2 instanceof PiglinEntity) {
            return;
        }
        if (PiglinBrain.hasItemInOffHand(arg)) {
            PiglinBrain.consumeOffHandItem(arg, false);
        }
        Brain<PiglinEntity> lv = arg.getBrain();
        lv.forget(MemoryModuleType.CELEBRATE_LOCATION);
        lv.forget(MemoryModuleType.DANCING);
        lv.forget(MemoryModuleType.ADMIRING_ITEM);
        if (arg2 instanceof PlayerEntity) {
            lv.remember(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
        }
        PiglinBrain.method_29536(arg).ifPresent(arg3 -> {
            if (arg3.getType() != arg2.getType()) {
                lv.forget(MemoryModuleType.AVOID_TARGET);
            }
        });
        if (arg.isBaby()) {
            lv.remember(MemoryModuleType.AVOID_TARGET, arg2, 100L);
            if (PiglinBrain.shouldAttack(arg2)) {
                PiglinBrain.angerAtCloserTargets(arg, arg2);
            }
            return;
        }
        if (arg2.getType() == EntityType.HOGLIN && PiglinBrain.hasOutnumberedHoglins(arg)) {
            PiglinBrain.runAwayFrom(arg, arg2);
            PiglinBrain.groupRunAwayFrom(arg, arg2);
            return;
        }
        PiglinBrain.tryRevenge(arg, arg2);
    }

    protected static void tryRevenge(AbstractPiglinEntity arg, LivingEntity arg2) {
        if (arg.getBrain().hasActivity(Activity.AVOID)) {
            return;
        }
        if (!PiglinBrain.shouldAttack(arg2)) {
            return;
        }
        if (LookTargetUtil.isNewTargetTooFar(arg, arg2, 4.0)) {
            return;
        }
        if (arg2.getType() == EntityType.PLAYER && arg.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            PiglinBrain.becomeAngryWithPlayer(arg, arg2);
            PiglinBrain.angerNearbyPiglins(arg);
        } else {
            PiglinBrain.becomeAngryWith(arg, arg2);
            PiglinBrain.angerAtCloserTargets(arg, arg2);
        }
    }

    public static Optional<SoundEvent> method_30091(PiglinEntity arg) {
        return arg.getBrain().getFirstPossibleNonCoreActivity().map(arg2 -> PiglinBrain.method_30087(arg, arg2));
    }

    private static SoundEvent method_30087(PiglinEntity arg, Activity arg2) {
        if (arg2 == Activity.FIGHT) {
            return SoundEvents.ENTITY_PIGLIN_ANGRY;
        }
        if (arg.method_30235()) {
            return SoundEvents.ENTITY_PIGLIN_RETREAT;
        }
        if (arg2 == Activity.AVOID && PiglinBrain.hasTargetToAvoid(arg)) {
            return SoundEvents.ENTITY_PIGLIN_RETREAT;
        }
        if (arg2 == Activity.ADMIRE_ITEM) {
            return SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM;
        }
        if (arg2 == Activity.CELEBRATE) {
            return SoundEvents.ENTITY_PIGLIN_CELEBRATE;
        }
        if (PiglinBrain.hasPlayerHoldingWantedItemNearby(arg)) {
            return SoundEvents.ENTITY_PIGLIN_JEALOUS;
        }
        if (PiglinBrain.hasSoulFireNearby(arg)) {
            return SoundEvents.ENTITY_PIGLIN_RETREAT;
        }
        return SoundEvents.ENTITY_PIGLIN_AMBIENT;
    }

    private static boolean hasTargetToAvoid(PiglinEntity arg) {
        Brain<PiglinEntity> lv = arg.getBrain();
        if (!lv.hasMemoryModule(MemoryModuleType.AVOID_TARGET)) {
            return false;
        }
        return lv.getOptionalMemory(MemoryModuleType.AVOID_TARGET).get().isInRange(arg, 12.0);
    }

    protected static boolean haveHuntedHoglinsRecently(PiglinEntity arg2) {
        return arg2.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY) || PiglinBrain.getNearbyVisiblePiglins(arg2).stream().anyMatch(arg -> arg.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY));
    }

    private static List<AbstractPiglinEntity> getNearbyVisiblePiglins(PiglinEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse((List<AbstractPiglinEntity>)ImmutableList.of());
    }

    private static List<AbstractPiglinEntity> getNearbyPiglins(AbstractPiglinEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse((List<AbstractPiglinEntity>)ImmutableList.of());
    }

    public static boolean wearsGoldArmor(LivingEntity arg) {
        Iterable<ItemStack> iterable = arg.getArmorItems();
        for (ItemStack lv : iterable) {
            Item lv2 = lv.getItem();
            if (!(lv2 instanceof ArmorItem) || ((ArmorItem)lv2).getMaterial() != ArmorMaterials.GOLD) continue;
            return true;
        }
        return false;
    }

    private static void stopWalking(PiglinEntity arg) {
        arg.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg.getNavigation().stop();
    }

    private static TimeLimitedTask<PiglinEntity> makeRememberRideableHoglinTask() {
        return new TimeLimitedTask<PiglinEntity>(new MemoryTransferTask<PiglinEntity, Entity>(PiglinEntity::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, RIDE_TARGET_MEMORY_DURATION), MEMORY_TRANSFER_TASK_DURATION);
    }

    protected static void angerAtCloserTargets(AbstractPiglinEntity arg, LivingEntity arg22) {
        PiglinBrain.getNearbyPiglins(arg).forEach(arg2 -> {
            if (!(arg22.getType() != EntityType.HOGLIN || arg2.canHunt() && ((HoglinEntity)arg22).canBeHunted())) {
                return;
            }
            PiglinBrain.angerAtIfCloser(arg2, arg22);
        });
    }

    protected static void angerNearbyPiglins(AbstractPiglinEntity arg2) {
        PiglinBrain.getNearbyPiglins(arg2).forEach(arg -> PiglinBrain.getNearestDetectedPlayer(arg).ifPresent(arg2 -> PiglinBrain.becomeAngryWith(arg, arg2)));
    }

    protected static void rememberGroupHunting(PiglinEntity arg) {
        PiglinBrain.getNearbyVisiblePiglins(arg).forEach(PiglinBrain::rememberHunting);
    }

    protected static void becomeAngryWith(AbstractPiglinEntity arg, LivingEntity arg2) {
        if (!PiglinBrain.shouldAttack(arg2)) {
            return;
        }
        arg.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        arg.getBrain().remember(MemoryModuleType.ANGRY_AT, arg2.getUuid(), 600L);
        if (arg2.getType() == EntityType.HOGLIN && arg.canHunt()) {
            PiglinBrain.rememberHunting(arg);
        }
        if (arg2.getType() == EntityType.PLAYER && arg.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            arg.getBrain().remember(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
    }

    private static void becomeAngryWithPlayer(AbstractPiglinEntity arg, LivingEntity arg2) {
        Optional<PlayerEntity> optional = PiglinBrain.getNearestDetectedPlayer(arg);
        if (optional.isPresent()) {
            PiglinBrain.becomeAngryWith(arg, optional.get());
        } else {
            PiglinBrain.becomeAngryWith(arg, arg2);
        }
    }

    private static void angerAtIfCloser(AbstractPiglinEntity arg, LivingEntity arg2) {
        Optional<LivingEntity> optional = PiglinBrain.getAngryAt(arg);
        LivingEntity lv = LookTargetUtil.getCloserEntity((LivingEntity)arg, optional, arg2);
        if (optional.isPresent() && optional.get() == lv) {
            return;
        }
        PiglinBrain.becomeAngryWith(arg, lv);
    }

    private static Optional<LivingEntity> getAngryAt(AbstractPiglinEntity arg) {
        return LookTargetUtil.getEntity(arg, MemoryModuleType.ANGRY_AT);
    }

    public static Optional<LivingEntity> method_29536(PiglinEntity arg) {
        if (arg.getBrain().hasMemoryModule(MemoryModuleType.AVOID_TARGET)) {
            return arg.getBrain().getOptionalMemory(MemoryModuleType.AVOID_TARGET);
        }
        return Optional.empty();
    }

    public static Optional<PlayerEntity> getNearestDetectedPlayer(AbstractPiglinEntity arg) {
        if (arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)) {
            return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
        }
        return Optional.empty();
    }

    private static void groupRunAwayFrom(PiglinEntity arg3, LivingEntity arg22) {
        PiglinBrain.getNearbyVisiblePiglins(arg3).stream().filter(arg -> arg instanceof PiglinEntity).forEach(arg2 -> PiglinBrain.runAwayFromClosestTarget((PiglinEntity)arg2, arg22));
    }

    private static void runAwayFromClosestTarget(PiglinEntity arg, LivingEntity arg2) {
        Brain<PiglinEntity> lv = arg.getBrain();
        LivingEntity lv2 = arg2;
        lv2 = LookTargetUtil.getCloserEntity((LivingEntity)arg, lv.getOptionalMemory(MemoryModuleType.AVOID_TARGET), lv2);
        lv2 = LookTargetUtil.getCloserEntity((LivingEntity)arg, lv.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), lv2);
        PiglinBrain.runAwayFrom(arg, lv2);
    }

    private static boolean shouldRunAwayFromHoglins(PiglinEntity arg) {
        Brain<PiglinEntity> lv = arg.getBrain();
        if (!lv.hasMemoryModule(MemoryModuleType.AVOID_TARGET)) {
            return true;
        }
        LivingEntity lv2 = lv.getOptionalMemory(MemoryModuleType.AVOID_TARGET).get();
        EntityType<?> lv3 = lv2.getType();
        if (lv3 == EntityType.HOGLIN) {
            return PiglinBrain.hasNoAdvantageAgainstHoglins(arg);
        }
        if (PiglinBrain.isZombified(lv3)) {
            return !lv.method_29519(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, lv2);
        }
        return false;
    }

    private static boolean hasNoAdvantageAgainstHoglins(PiglinEntity arg) {
        return !PiglinBrain.hasOutnumberedHoglins(arg);
    }

    private static boolean hasOutnumberedHoglins(PiglinEntity arg) {
        int i = arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
        int j = arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
        return j > i;
    }

    private static void runAwayFrom(PiglinEntity arg, LivingEntity arg2) {
        arg.getBrain().forget(MemoryModuleType.ANGRY_AT);
        arg.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        arg.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg.getBrain().remember(MemoryModuleType.AVOID_TARGET, arg2, AVOID_MEMORY_DURATION.choose(arg.world.random));
        PiglinBrain.rememberHunting(arg);
    }

    protected static void rememberHunting(AbstractPiglinEntity arg) {
        arg.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, true, HUNT_MEMORY_DURATION.choose(arg.world.random));
    }

    private static void setEatenRecently(PiglinEntity arg) {
        arg.getBrain().remember(MemoryModuleType.ATE_RECENTLY, true, 200L);
    }

    private static Vec3d findGround(PiglinEntity arg) {
        Vec3d lv = TargetFinder.findGroundTarget(arg, 4, 2);
        return lv == null ? arg.getPos() : lv;
    }

    private static boolean hasAteRecently(PiglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.ATE_RECENTLY);
    }

    protected static boolean hasIdleActivity(AbstractPiglinEntity arg) {
        return arg.getBrain().hasActivity(Activity.IDLE);
    }

    private static boolean isHoldingCrossbow(LivingEntity arg) {
        return arg.isHolding(Items.CROSSBOW);
    }

    private static void setAdmiringItem(LivingEntity arg) {
        arg.getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, 120L);
    }

    private static boolean isAdmiringItem(PiglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_ITEM);
    }

    private static boolean acceptsForBarter(Item arg) {
        return arg == BARTERING_ITEM;
    }

    private static boolean isFood(Item arg) {
        return FOOD.contains(arg);
    }

    private static boolean shouldAttack(LivingEntity arg) {
        return EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(arg);
    }

    private static boolean hasSoulFireNearby(PiglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean hasPlayerHoldingWantedItemNearby(LivingEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static boolean canWander(LivingEntity arg) {
        return !PiglinBrain.hasPlayerHoldingWantedItemNearby(arg);
    }

    public static boolean isGoldHoldingPlayer(LivingEntity arg) {
        return arg.getType() == EntityType.PLAYER && arg.isHolding(PiglinBrain::isGoldenItem);
    }

    private static boolean hasBeenHitByPlayer(PiglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_DISABLED);
    }

    private static boolean hasBeenHurt(LivingEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }

    private static boolean hasItemInOffHand(PiglinEntity arg) {
        return !arg.getOffHandStack().isEmpty();
    }

    private static boolean doesNotHaveGoldInOffHand(PiglinEntity arg) {
        return arg.getOffHandStack().isEmpty() || !PiglinBrain.isGoldenItem(arg.getOffHandStack().getItem());
    }

    public static boolean isZombified(EntityType arg) {
        return arg == EntityType.ZOMBIFIED_PIGLIN || arg == EntityType.ZOGLIN;
    }
}

