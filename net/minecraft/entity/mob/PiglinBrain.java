/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
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
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
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
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.DynamicSerializableBoolean;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;

public class PiglinBrain {
    protected static final Item BARTERING_ITEM = Items.GOLD_INGOT;
    private static final IntRange field_22477 = IntRange.between(10, 20);
    private static final IntRange HUNT_MEMORY_DURATION = Durations.betweenSeconds(30, 120);
    private static final IntRange MEMORY_TRANSFER_TASK_DURATION = Durations.betweenSeconds(10, 40);
    private static final IntRange RIDE_TARGET_MEMORY_DURATION = Durations.betweenSeconds(10, 30);
    private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);
    private static final Set FOOD = ImmutableSet.of((Object)Items.PORKCHOP, (Object)Items.COOKED_PORKCHOP);

    protected static Brain<?> create(PiglinEntity arg, Dynamic<?> dynamic) {
        Brain<PiglinEntity> lv = new Brain<PiglinEntity>((Collection<MemoryModuleType<?>>)PiglinEntity.MEMORY_MODULE_TYPES, (Collection<SensorType<Sensor<PiglinEntity>>>)PiglinEntity.SENSOR_TYPES, dynamic);
        PiglinBrain.addCoreActivities(lv);
        PiglinBrain.addIdleActivities(lv);
        PiglinBrain.addAdmireItemActivities(lv);
        PiglinBrain.addFightActivities(arg, lv);
        PiglinBrain.addCelebrateActivities(lv);
        PiglinBrain.addAvoidActivities(lv);
        PiglinBrain.addRideActivities(lv);
        lv.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        lv.setDefaultActivity(Activity.IDLE);
        lv.resetPossibleActivities();
        return lv;
    }

    protected static void setHuntedRecently(PiglinEntity arg) {
        int i = HUNT_MEMORY_DURATION.choose(arg.world.random);
        arg.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, DynamicSerializableBoolean.of(true), i);
    }

    private static void addCoreActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.CORE, 0, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(200), (Object)new OpenDoorsTask(), new RemoveOffHandItemTask(), new AdmireItemTask(120), (Object)new DefeatTargetTask(300), new ForgetAngryAtTargetTask()));
    }

    private static void addIdleActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of((Object)new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0f), new UpdateAttackTargetTask<PiglinEntity>(PiglinEntity::isAdult, PiglinBrain::getPreferredTarget), new ConditionalTask<PiglinEntity>(PiglinEntity::canHunt, new HuntHoglinTask()), PiglinBrain.makeGoToZombifiedPiglinTask(), PiglinBrain.makeGoToSoulFireTask(), PiglinBrain.makeRememberRideableHoglinTask(), PiglinBrain.makeRandomFollowTask(), PiglinBrain.makeRandomWanderTask(), (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)));
    }

    private static void addFightActivities(PiglinEntity arg, Brain<PiglinEntity> arg22) {
        arg22.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(new ForgetAttackTargetTask(arg2 -> !PiglinBrain.isPreferredAttackTarget(arg, arg2)), new ConditionalTask<PiglinEntity>(PiglinBrain::isHoldingCrossbow, new AttackTask(5, 0.75f)), (Object)new RangedApproachTask(1.0f), (Object)new MeleeAttackTask(20), new CrossbowAttackTask(), new HuntFinishTask()), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addCelebrateActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.CELEBRATE, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(PiglinBrain.makeGoToZombifiedPiglinTask(), PiglinBrain.makeGoToSoulFireTask(), (Object)new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0f), new UpdateAttackTargetTask<PiglinEntity>(PiglinEntity::isAdult, PiglinBrain::getPreferredTarget), new GoToCelebrateTask(2, 1.0f), new RandomTask(ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of((Object)new StrollTask(0.6f, 2, 1), (Object)1), (Object)Pair.of((Object)new WaitTask(10, 20), (Object)1)))), MemoryModuleType.CELEBRATE_LOCATION);
    }

    private static void addAdmireItemActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.ADMIRE_ITEM, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(new WalkToNearestVisibleWantedItemTask<PiglinEntity>(PiglinBrain::doesNotHaveGoldInOffHand, 1.0f, true, 9), new ConditionalTask<PiglinEntity>(PiglinBrain::hasItemInOffHand, PiglinBrain.method_24916()), new WantNewItemTask(9)), MemoryModuleType.ADMIRING_ITEM);
    }

    private static void addAvoidActivities(Brain<PiglinEntity> arg) {
        arg.setTaskList(Activity.AVOID, 10, (ImmutableList<Task<PiglinEntity>>)ImmutableList.of(GoToRememberedPositionTask.toEntity(MemoryModuleType.AVOID_TARGET, 1.1f, 6, false), PiglinBrain.makeRandomFollowTask(), PiglinBrain.makeRandomWanderTask(), new ForgetTask<PiglinEntity>(PiglinBrain::shouldRunAwayFromHoglins, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
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

    private static Task<PiglinEntity> method_24916() {
        return new TimeLimitedTask<MobEntityWithAi>(new StrollTask(0.3f, 1, 0), field_22477);
    }

    private static GoToRememberedPositionTask<BlockPos> makeGoToSoulFireTask() {
        return GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.1f, 8, false);
    }

    private static GoToRememberedPositionTask<?> makeGoToZombifiedPiglinTask() {
        return GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, 1.1f, 10, false);
    }

    protected static void tickActivities(PiglinEntity arg) {
        Brain<PiglinEntity> lv = arg.getBrain();
        Activity lv2 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        lv.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.ADMIRE_ITEM, (Object)Activity.FIGHT, (Object)Activity.AVOID, (Object)Activity.CELEBRATE, (Object)Activity.RIDE, (Object)Activity.IDLE));
        Activity lv3 = lv.getFirstPossibleNonCoreActivity().orElse(null);
        if (lv2 != lv3) {
            PiglinBrain.playSound(arg);
        }
        arg.setAttacking(lv.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
        if (!lv.hasMemoryModule(MemoryModuleType.RIDE_TARGET)) {
            arg.stopRiding();
        }
        if (arg.hasVehicle() && PiglinBrain.hasPlayerHoldingWantedItemNearby(arg)) {
            arg.stopRiding();
            arg.getBrain().forget(MemoryModuleType.RIDE_TARGET);
        }
    }

    protected static void loot(PiglinEntity arg, ItemEntity arg2) {
        PiglinBrain.stopWalking(arg);
        arg.sendPickup(arg2, 1);
        ItemStack lv = PiglinBrain.method_24848(arg2);
        Item lv2 = lv.getItem();
        if (PiglinBrain.isGoldenItem(lv2)) {
            if (PiglinBrain.hasItemInOffHand(arg)) {
                arg.dropStack(arg.getStackInHand(Hand.OFF_HAND));
            }
            arg.equipToOffHand(lv);
            PiglinBrain.setAdmiringItem(arg);
            return;
        }
        if (PiglinBrain.isFood(lv2) && !PiglinBrain.hasAteRecently(arg)) {
            PiglinBrain.setEatenRecently(arg);
            return;
        }
        boolean bl = arg.tryEquip(lv);
        if (bl) {
            return;
        }
        PiglinBrain.method_24849(arg, lv);
    }

    private static ItemStack method_24848(ItemEntity arg) {
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
        if (arg.isAdult()) {
            if (bl && PiglinBrain.acceptsForBarter(lv.getItem())) {
                PiglinBrain.doBarter(arg, PiglinBrain.getBarteredItem(arg));
            } else {
                boolean bl2 = arg.tryEquip(lv);
                if (!bl2) {
                    PiglinBrain.method_24849(arg, lv);
                }
            }
        } else {
            boolean bl3 = arg.tryEquip(lv);
            if (!bl3) {
                ItemStack lv2 = arg.getMainHandStack();
                if (PiglinBrain.isGoldenItem(lv2.getItem())) {
                    PiglinBrain.method_24849(arg, lv2);
                } else {
                    PiglinBrain.doBarter(arg, Collections.singletonList(lv2));
                }
                arg.equipToMainHand(lv);
            }
        }
    }

    protected static void method_25948(PiglinEntity arg) {
        if (PiglinBrain.isAdmiringItem(arg) && !arg.getOffHandStack().isEmpty()) {
            arg.dropStack(arg.getOffHandStack());
            arg.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        }
    }

    private static void method_24849(PiglinEntity arg, ItemStack arg2) {
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

    private static Optional<? extends LivingEntity> getPreferredTarget(PiglinEntity arg) {
        Brain<PiglinEntity> lv = arg.getBrain();
        Optional<LivingEntity> optional = LookTargetUtil.getEntity(arg, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && PiglinBrain.shouldAttack(optional.get())) {
            return optional;
        }
        Optional<WitherSkeletonEntity> optional2 = lv.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WITHER_SKELETON);
        if (optional2.isPresent()) {
            return optional2;
        }
        Optional<PlayerEntity> optional3 = lv.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
        if (optional3.isPresent() && PiglinBrain.shouldAttack(optional3.get())) {
            return optional3;
        }
        return Optional.empty();
    }

    public static void onGoldBlockBroken(PlayerEntity arg) {
        if (!PiglinBrain.shouldAttack(arg)) {
            return;
        }
        List<PiglinEntity> list = arg.world.getNonSpectatingEntities(PiglinEntity.class, arg.getBoundingBox().expand(16.0));
        list.stream().filter(PiglinBrain::hasIdleActivity).filter(arg2 -> LookTargetUtil.isVisibleInMemory(arg2, arg)).forEach(arg2 -> PiglinBrain.angerAt(arg2, arg));
    }

    public static boolean playerInteract(PiglinEntity arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        if (PiglinBrain.method_27086(arg, lv)) {
            ItemStack lv2 = lv.split(1);
            arg.equipToOffHand(lv2);
            PiglinBrain.setAdmiringItem(arg);
            return true;
        }
        return false;
    }

    protected static boolean method_27086(PiglinEntity arg, ItemStack arg2) {
        return !PiglinBrain.hasBeenHitByPlayer(arg) && !PiglinBrain.isAdmiringItem(arg) && arg.isAdult() && PiglinBrain.acceptsForBarter(arg2.getItem());
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
        lv.forget(MemoryModuleType.ADMIRING_ITEM);
        if (arg2 instanceof PlayerEntity) {
            lv.remember(MemoryModuleType.ADMIRING_DISABLED, DynamicSerializableBoolean.of(true), 400L);
        }
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

    private static void tryRevenge(PiglinEntity arg, LivingEntity arg2) {
        if (arg.getBrain().hasActivity(Activity.AVOID) && arg2.getType() == EntityType.HOGLIN) {
            return;
        }
        if (!PiglinBrain.shouldAttack(arg2)) {
            return;
        }
        if (LookTargetUtil.isNewTargetTooFar(arg, arg2, 4.0)) {
            return;
        }
        PiglinBrain.angerAt(arg, arg2);
        PiglinBrain.angerAtCloserTargets(arg, arg2);
    }

    private static void playSound(PiglinEntity arg) {
        arg.getBrain().getFirstPossibleNonCoreActivity().ifPresent(arg2 -> {
            if (arg2 == Activity.FIGHT) {
                arg.playAngrySound();
            } else if (arg2 == Activity.AVOID || arg.canConvert()) {
                arg.playRetreatSound();
            } else if (arg2 == Activity.ADMIRE_ITEM) {
                arg.playAdmireItemSound();
            } else if (arg2 == Activity.CELEBRATE) {
                arg.playCelebrateSound();
            } else if (PiglinBrain.hasPlayerHoldingWantedItemNearby((LivingEntity)arg)) {
                arg.playJealousSound();
            } else if (PiglinBrain.hasZombifiedPiglinNearby(arg) || PiglinBrain.hasSoulFireNearby(arg)) {
                arg.playRetreatSound();
            }
        });
    }

    protected static void playSoundAtChance(PiglinEntity arg) {
        if ((double)arg.world.random.nextFloat() < 0.0125) {
            PiglinBrain.playSound(arg);
        }
    }

    protected static boolean haveHuntedHoglinsRecently(PiglinEntity arg2) {
        return arg2.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY) || PiglinBrain.getNearbyVisiblePiglins(arg2).stream().anyMatch(arg -> arg.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY));
    }

    private static List<PiglinEntity> getNearbyVisiblePiglins(PiglinEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse((List<PiglinEntity>)ImmutableList.of());
    }

    private static List<PiglinEntity> getNearbyPiglins(PiglinEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ADULT_PIGLINS).orElse((List<PiglinEntity>)ImmutableList.of());
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

    protected static void angerAtCloserTargets(PiglinEntity arg, LivingEntity arg22) {
        PiglinBrain.getNearbyPiglins(arg).forEach(arg2 -> {
            if (!(arg22.getType() != EntityType.HOGLIN || arg2.canHunt() && ((HoglinEntity)arg22).canBeHunted())) {
                return;
            }
            PiglinBrain.angerAtIfCloser(arg2, arg22);
        });
    }

    protected static void rememberGroupHunting(PiglinEntity arg) {
        PiglinBrain.getNearbyVisiblePiglins(arg).forEach(PiglinBrain::rememberHunting);
    }

    protected static void angerAt(PiglinEntity arg, LivingEntity arg2) {
        arg.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        arg.getBrain().remember(MemoryModuleType.ANGRY_AT, new DynamicSerializableUuid(arg2.getUuid()), 600L);
        if (arg2.getType() == EntityType.HOGLIN) {
            PiglinBrain.rememberHunting(arg);
        }
    }

    private static void angerAtIfCloser(PiglinEntity arg, LivingEntity arg2) {
        Optional<LivingEntity> optional = PiglinBrain.getAngryAt(arg);
        LivingEntity lv = LookTargetUtil.getCloserEntity((LivingEntity)arg, optional, arg2);
        if (optional.isPresent() && optional.get() == lv) {
            return;
        }
        PiglinBrain.angerAt(arg, lv);
    }

    private static Optional<LivingEntity> getAngryAt(PiglinEntity arg) {
        return LookTargetUtil.getEntity(arg, MemoryModuleType.ANGRY_AT);
    }

    private static void groupRunAwayFrom(PiglinEntity arg, LivingEntity arg22) {
        PiglinBrain.getNearbyVisiblePiglins(arg).forEach(arg2 -> PiglinBrain.runAwayFromClosestTarget(arg2, arg22));
    }

    private static void runAwayFromClosestTarget(PiglinEntity arg, LivingEntity arg2) {
        Brain<PiglinEntity> lv = arg.getBrain();
        LivingEntity lv2 = arg2;
        lv2 = LookTargetUtil.getCloserEntity((LivingEntity)arg, lv.getOptionalMemory(MemoryModuleType.AVOID_TARGET), lv2);
        lv2 = LookTargetUtil.getCloserEntity((LivingEntity)arg, lv.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), lv2);
        PiglinBrain.runAwayFrom(arg, lv2);
    }

    private static boolean shouldRunAwayFromHoglins(PiglinEntity arg) {
        return arg.isAdult() && PiglinBrain.hasNoAdvantageAgainstHoglins(arg);
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
        arg.getBrain().remember(MemoryModuleType.AVOID_TARGET, arg2, AVOID_MEMORY_DURATION.choose(arg.world.random));
        PiglinBrain.rememberHunting(arg);
    }

    protected static void rememberHunting(PiglinEntity arg) {
        arg.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, DynamicSerializableBoolean.of(true), HUNT_MEMORY_DURATION.choose(arg.world.random));
    }

    private static boolean hasPlayerHoldingWantedItemNearby(PiglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
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

    protected static boolean hasIdleActivity(PiglinEntity arg) {
        return arg.getBrain().hasActivity(Activity.IDLE);
    }

    private static boolean isHoldingCrossbow(LivingEntity arg) {
        return arg.isHolding(Items.CROSSBOW);
    }

    private static void setAdmiringItem(LivingEntity arg) {
        arg.getBrain().remember(MemoryModuleType.ADMIRING_ITEM, DynamicSerializableBoolean.of(true), 120L);
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

    private static boolean hasZombifiedPiglinNearby(PiglinEntity arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED);
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
}

