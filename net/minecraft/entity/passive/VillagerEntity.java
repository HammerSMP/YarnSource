/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.class_5425;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerGossips;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.Logger;

public class VillagerEntity
extends AbstractTraderEntity
implements InteractionObserver,
VillagerDataContainer {
    private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of((Object)Items.BREAD, (Object)4, (Object)Items.POTATO, (Object)1, (Object)Items.CARROT, (Object)1, (Object)Items.BEETROOT, (Object)1);
    private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of((Object)Items.BREAD, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT, (Object[])new Item[]{Items.BEETROOT_SEEDS});
    private int levelUpTimer;
    private boolean levelingUp;
    @Nullable
    private PlayerEntity lastCustomer;
    private byte foodLevel;
    private final VillagerGossips gossip = new VillagerGossips();
    private long gossipStartTime;
    private long lastGossipDecayTime;
    private int experience;
    private long lastRestockTime;
    private int restocksToday;
    private long lastRestockCheckTime;
    private boolean natural;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, (Object[])new MemoryModuleType[]{MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY});
    private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POINTS_OF_INTEREST = ImmutableMap.of(MemoryModuleType.HOME, (arg, arg2) -> arg2 == PointOfInterestType.HOME, MemoryModuleType.JOB_SITE, (arg, arg2) -> arg.getVillagerData().getProfession().getWorkStation() == arg2, MemoryModuleType.POTENTIAL_JOB_SITE, (arg, arg2) -> PointOfInterestType.IS_USED_BY_PROFESSION.test((PointOfInterestType)arg2), MemoryModuleType.MEETING_POINT, (arg, arg2) -> arg2 == PointOfInterestType.MEETING);

    public VillagerEntity(EntityType<? extends VillagerEntity> arg, World arg2) {
        this(arg, arg2, VillagerType.PLAINS);
    }

    public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type) {
        super((EntityType<? extends AbstractTraderEntity>)entityType, world);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().withType(type).withProfession(VillagerProfession.NONE));
    }

    public Brain<VillagerEntity> getBrain() {
        return super.getBrain();
    }

    protected Brain.Profile<VillagerEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<VillagerEntity> lv = this.createBrainProfile().deserialize(dynamic);
        this.initBrain(lv);
        return lv;
    }

    public void reinitializeBrain(ServerWorld world) {
        Brain<VillagerEntity> lv = this.getBrain();
        lv.stopAllTasks(world, this);
        this.brain = lv.copy();
        this.initBrain(this.getBrain());
    }

    private void initBrain(Brain<VillagerEntity> brain) {
        VillagerProfession lv = this.getVillagerData().getProfession();
        if (this.isBaby()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.setTaskList(Activity.PLAY, VillagerTaskListProvider.createPlayTasks(0.5f));
        } else {
            brain.setSchedule(Schedule.VILLAGER_DEFAULT);
            brain.setTaskList(Activity.WORK, (ImmutableList<Pair<Integer, Task<VillagerEntity>>>)VillagerTaskListProvider.createWorkTasks(lv, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT))));
        }
        brain.setTaskList(Activity.CORE, VillagerTaskListProvider.createCoreTasks(lv, 0.5f));
        brain.setTaskList(Activity.MEET, (ImmutableList<Pair<Integer, Task<VillagerEntity>>>)VillagerTaskListProvider.createMeetTasks(lv, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryModuleState.VALUE_PRESENT))));
        brain.setTaskList(Activity.REST, VillagerTaskListProvider.createRestTasks(lv, 0.5f));
        brain.setTaskList(Activity.IDLE, VillagerTaskListProvider.createIdleTasks(lv, 0.5f));
        brain.setTaskList(Activity.PANIC, VillagerTaskListProvider.createPanicTasks(lv, 0.5f));
        brain.setTaskList(Activity.PRE_RAID, VillagerTaskListProvider.createPreRaidTasks(lv, 0.5f));
        brain.setTaskList(Activity.RAID, VillagerTaskListProvider.createRaidTasks(lv, 0.5f));
        brain.setTaskList(Activity.HIDE, VillagerTaskListProvider.createHideTasks(lv, 0.5f));
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.doExclusively(Activity.IDLE);
        brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();
        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld)this.world);
        }
    }

    public static DefaultAttributeContainer.Builder createVillagerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    public boolean isNatural() {
        return this.natural;
    }

    @Override
    protected void mobTick() {
        Raid lv;
        this.world.getProfiler().push("villagerBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        if (this.natural) {
            this.natural = false;
        }
        if (!this.hasCustomer() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levelingUp) {
                    this.levelUp();
                    this.levelingUp = false;
                }
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastCustomer != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
            this.world.sendEntityStatus(this, (byte)14);
            this.lastCustomer = null;
        }
        if (!this.isAiDisabled() && this.random.nextInt(100) == 0 && (lv = ((ServerWorld)this.world).getRaidAt(this.getBlockPos())) != null && lv.isActive() && !lv.isFinished()) {
            this.world.sendEntityStatus(this, (byte)42);
        }
        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.hasCustomer()) {
            this.resetCustomer();
        }
        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHeadRollingTimeLeft() > 0) {
            this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
        }
        this.decayGossip();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack lv = player.getStackInHand(hand);
        if (lv.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return ActionResult.success(this.world.isClient);
            }
            boolean bl = this.getOffers().isEmpty();
            if (hand == Hand.MAIN_HAND) {
                if (bl && !this.world.isClient) {
                    this.sayNo();
                }
                player.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (bl) {
                return ActionResult.success(this.world.isClient);
            }
            if (!this.world.isClient && !this.offers.isEmpty()) {
                this.beginTradeWith(player);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(player, hand);
    }

    private void sayNo() {
        this.setHeadRollingTimeLeft(40);
        if (!this.world.isClient()) {
            this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private void beginTradeWith(PlayerEntity customer) {
        this.prepareRecipesFor(customer);
        this.setCurrentCustomer(customer);
        this.sendOffers(customer, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity customer) {
        boolean bl = this.getCurrentCustomer() != null && customer == null;
        super.setCurrentCustomer(customer);
        if (bl) {
            this.resetCustomer();
        }
    }

    @Override
    protected void resetCustomer() {
        super.resetCustomer();
        this.clearCurrentBonus();
    }

    private void clearCurrentBonus() {
        for (TradeOffer lv : this.getOffers()) {
            lv.clearSpecialPrice();
        }
    }

    @Override
    public boolean canRefreshTrades() {
        return true;
    }

    public void restock() {
        this.updatePricesOnDemand();
        for (TradeOffer lv : this.getOffers()) {
            lv.resetUses();
        }
        this.lastRestockTime = this.world.getTime();
        ++this.restocksToday;
    }

    private boolean needRestock() {
        for (TradeOffer lv : this.getOffers()) {
            if (!lv.method_21834()) continue;
            return true;
        }
        return false;
    }

    private boolean canRestock() {
        return this.restocksToday == 0 || this.restocksToday < 2 && this.world.getTime() > this.lastRestockTime + 2400L;
    }

    public boolean shouldRestock() {
        long l = this.lastRestockTime + 12000L;
        long m = this.world.getTime();
        boolean bl = m > l;
        long n = this.world.getTimeOfDay();
        if (this.lastRestockCheckTime > 0L) {
            long p = n / 24000L;
            long o = this.lastRestockCheckTime / 24000L;
            bl |= p > o;
        }
        this.lastRestockCheckTime = n;
        if (bl) {
            this.lastRestockTime = m;
            this.clearDailyRestockCount();
        }
        return this.canRestock() && this.needRestock();
    }

    private void method_21723() {
        int i = 2 - this.restocksToday;
        if (i > 0) {
            for (TradeOffer lv : this.getOffers()) {
                lv.resetUses();
            }
        }
        for (int j = 0; j < i; ++j) {
            this.updatePricesOnDemand();
        }
    }

    private void updatePricesOnDemand() {
        for (TradeOffer lv : this.getOffers()) {
            lv.updatePriceOnDemand();
        }
    }

    private void prepareRecipesFor(PlayerEntity player) {
        int i = this.getReputation(player);
        if (i != 0) {
            for (TradeOffer lv : this.getOffers()) {
                lv.increaseSpecialPrice(-MathHelper.floor((float)i * lv.getPriceMultiplier()));
            }
        }
        if (player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            StatusEffectInstance lv2 = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
            int j = lv2.getAmplifier();
            for (TradeOffer lv3 : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double)j;
                int k = (int)Math.floor(d * (double)lv3.getOriginalFirstBuyItem().getCount());
                lv3.increaseSpecialPrice(-Math.max(k, 1));
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        VillagerData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.getVillagerData()).resultOrPartial(((Logger)LOGGER)::error).ifPresent(arg2 -> tag.put("VillagerData", (Tag)arg2));
        tag.putByte("FoodLevel", this.foodLevel);
        tag.put("Gossips", (Tag)this.gossip.serialize(NbtOps.INSTANCE).getValue());
        tag.putInt("Xp", this.experience);
        tag.putLong("LastRestock", this.lastRestockTime);
        tag.putLong("LastGossipDecay", this.lastGossipDecayTime);
        tag.putInt("RestocksToday", this.restocksToday);
        if (this.natural) {
            tag.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("VillagerData", 10)) {
            DataResult dataResult = VillagerData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag.get("VillagerData")));
            dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(this::setVillagerData);
        }
        if (tag.contains("Offers", 10)) {
            this.offers = new TraderOfferList(tag.getCompound("Offers"));
        }
        if (tag.contains("FoodLevel", 1)) {
            this.foodLevel = tag.getByte("FoodLevel");
        }
        ListTag lv = tag.getList("Gossips", 10);
        this.gossip.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)lv));
        if (tag.contains("Xp", 3)) {
            this.experience = tag.getInt("Xp");
        }
        this.lastRestockTime = tag.getLong("LastRestock");
        this.lastGossipDecayTime = tag.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld)this.world);
        }
        this.restocksToday = tag.getInt("RestocksToday");
        if (tag.contains("AssignProfessionWhenSpawned")) {
            this.natural = tag.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.hasCustomer()) {
            return SoundEvents.ENTITY_VILLAGER_TRADE;
        }
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    public void playWorkSound() {
        SoundEvent lv = this.getVillagerData().getProfession().getWorkSound();
        if (lv != null) {
            this.playSound(lv, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    public void setVillagerData(VillagerData villagerData) {
        VillagerData lv = this.getVillagerData();
        if (lv.getProfession() != villagerData.getProfession()) {
            this.offers = null;
        }
        this.dataTracker.set(VILLAGER_DATA, villagerData);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.dataTracker.get(VILLAGER_DATA);
    }

    @Override
    protected void afterUsing(TradeOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.experience += offer.getTraderExperience();
        this.lastCustomer = this.getCurrentCustomer();
        if (this.canLevelUp()) {
            this.levelUpTimer = 40;
            this.levelingUp = true;
            i += 5;
        }
        if (offer.shouldRewardPlayerExperience()) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    @Override
    public void setAttacker(@Nullable LivingEntity attacker) {
        if (attacker != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
            if (this.isAlive() && attacker instanceof PlayerEntity) {
                this.world.sendEntityStatus(this, (byte)13);
            }
        }
        super.setAttacker(attacker);
    }

    @Override
    public void onDeath(DamageSource source) {
        LOGGER.info("Villager {} died, message: '{}'", (Object)this, (Object)source.getDeathMessage(this).getString());
        Entity lv = source.getAttacker();
        if (lv != null) {
            this.notifyDeath(lv);
        }
        this.releaseTicketFor(MemoryModuleType.HOME);
        this.releaseTicketFor(MemoryModuleType.JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
        super.onDeath(source);
    }

    private void notifyDeath(Entity killer) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        Optional<List<LivingEntity>> optional = this.brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
        if (!optional.isPresent()) {
            return;
        }
        ServerWorld lv = (ServerWorld)this.world;
        optional.get().stream().filter(arg -> arg instanceof InteractionObserver).forEach(arg3 -> lv.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver)((Object)arg3)));
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> arg) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerWorld)this.world).getServer();
        this.brain.getOptionalMemory(arg).ifPresent(arg2 -> {
            ServerWorld lv = minecraftServer.getWorld(arg2.getDimension());
            if (lv == null) {
                return;
            }
            PointOfInterestStorage lv2 = lv.getPointOfInterestStorage();
            Optional<PointOfInterestType> optional = lv2.getType(arg2.getPos());
            BiPredicate<VillagerEntity, PointOfInterestType> biPredicate = POINTS_OF_INTEREST.get(arg);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                lv2.releaseTicket(arg2.getPos());
                DebugInfoSender.sendPointOfInterest(lv, arg2.getPos());
            }
        });
    }

    @Override
    public boolean isReadyToBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && this.getBreedingAge() == 0;
    }

    private boolean lacksFood() {
        return this.foodLevel < 12;
    }

    private void consumeAvailableFood() {
        if (!this.lacksFood() || this.getAvailableFood() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().size(); ++i) {
            int j;
            Integer integer;
            ItemStack lv = this.getInventory().getStack(i);
            if (lv.isEmpty() || (integer = ITEM_FOOD_VALUES.get(lv.getItem())) == null) continue;
            for (int k = j = lv.getCount(); k > 0; --k) {
                this.foodLevel = (byte)(this.foodLevel + integer);
                this.getInventory().removeStack(i, 1);
                if (this.lacksFood()) continue;
                return;
            }
        }
    }

    public int getReputation(PlayerEntity player) {
        return this.gossip.getReputationFor(player.getUuid(), arg -> true);
    }

    private void depleteFood(int amount) {
        this.foodLevel = (byte)(this.foodLevel - amount);
    }

    public void eatForBreeding() {
        this.consumeAvailableFood();
        this.depleteFood(12);
    }

    public void setOffers(TraderOfferList offers) {
        this.offers = offers;
    }

    private boolean canLevelUp() {
        int i = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(i) && this.experience >= VillagerData.getUpperLevelExperience(i);
    }

    private void levelUp() {
        this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
        this.fillRecipes();
    }

    @Override
    protected Text getDefaultName() {
        return new TranslatableText(this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getId(this.getVillagerData().getProfession()).getPath());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 12) {
            this.produceParticles(ParticleTypes.HEART);
        } else if (status == 13) {
            this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
        } else if (status == 14) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        } else if (status == 42) {
            this.produceParticles(ParticleTypes.SPLASH);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        if (spawnReason == SpawnReason.BREEDING) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }
        if (spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.DISPENSER) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(arg.getBiome(this.getBlockPos()))));
        }
        if (spawnReason == SpawnReason.STRUCTURE) {
            this.natural = true;
        }
        return super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
    }

    @Override
    public VillagerEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        VillagerType lv3;
        double d = this.random.nextDouble();
        if (d < 0.5) {
            VillagerType lv = VillagerType.forBiome(arg.getBiome(this.getBlockPos()));
        } else if (d < 0.75) {
            VillagerType lv2 = this.getVillagerData().getType();
        } else {
            lv3 = ((VillagerEntity)arg2).getVillagerData().getType();
        }
        VillagerEntity lv4 = new VillagerEntity(EntityType.VILLAGER, arg, lv3);
        lv4.initialize(arg, arg.getLocalDifficulty(lv4.getBlockPos()), SpawnReason.BREEDING, null, null);
        return lv4;
    }

    @Override
    public void onStruckByLightning(ServerWorld arg, LightningEntity arg2) {
        if (arg.getDifficulty() != Difficulty.PEACEFUL) {
            LOGGER.info("Villager {} was struck by lightning {}.", (Object)this, (Object)arg2);
            WitchEntity lv = EntityType.WITCH.create(arg);
            lv.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
            lv.initialize(arg, arg.getLocalDifficulty(lv.getBlockPos()), SpawnReason.CONVERSION, null, null);
            lv.setAiDisabled(this.isAiDisabled());
            if (this.hasCustomName()) {
                lv.setCustomName(this.getCustomName());
                lv.setCustomNameVisible(this.isCustomNameVisible());
            }
            lv.setPersistent();
            arg.spawnEntity(lv);
            this.remove();
        } else {
            super.onStruckByLightning(arg, arg2);
        }
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack lv = item.getStack();
        if (this.canGather(lv)) {
            SimpleInventory lv2 = this.getInventory();
            boolean bl = lv2.canInsert(lv);
            if (!bl) {
                return;
            }
            this.method_29499(item);
            this.sendPickup(item, lv.getCount());
            ItemStack lv3 = lv2.addStack(lv);
            if (lv3.isEmpty()) {
                item.remove();
            } else {
                lv.setCount(lv3.getCount());
            }
        }
    }

    @Override
    public boolean canGather(ItemStack stack) {
        Item lv = stack.getItem();
        return (GATHERABLE_ITEMS.contains(lv) || this.getVillagerData().getProfession().getGatherableItems().contains((Object)lv)) && this.getInventory().canInsert(stack);
    }

    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }

    public boolean canBreed() {
        return this.getAvailableFood() < 12;
    }

    private int getAvailableFood() {
        SimpleInventory lv = this.getInventory();
        return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(entry -> lv.count((Item)entry.getKey()) * (Integer)entry.getValue()).sum();
    }

    public boolean hasSeedToPlant() {
        return this.getInventory().containsAny((Set<Item>)ImmutableSet.of((Object)Items.WHEAT_SEEDS, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.BEETROOT_SEEDS));
    }

    @Override
    protected void fillRecipes() {
        VillagerData lv = this.getVillagerData();
        Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(lv.getProfession());
        if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
            return;
        }
        TradeOffers.Factory[] lvs = (TradeOffers.Factory[])int2ObjectMap.get(lv.getLevel());
        if (lvs == null) {
            return;
        }
        TraderOfferList lv2 = this.getOffers();
        this.fillRecipesFromPool(lv2, lvs, 2);
    }

    public void talkWithVillager(ServerWorld arg, VillagerEntity arg2, long l) {
        if (l >= this.gossipStartTime && l < this.gossipStartTime + 1200L || l >= arg2.gossipStartTime && l < arg2.gossipStartTime + 1200L) {
            return;
        }
        this.gossip.shareGossipFrom(arg2.gossip, this.random, 10);
        this.gossipStartTime = l;
        arg2.gossipStartTime = l;
        this.summonGolem(arg, l, 5);
    }

    private void decayGossip() {
        long l = this.world.getTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = l;
            return;
        }
        if (l < this.lastGossipDecayTime + 24000L) {
            return;
        }
        this.gossip.decay();
        this.lastGossipDecayTime = l;
    }

    public void summonGolem(ServerWorld arg2, long l, int i) {
        if (!this.canSummonGolem(l)) {
            return;
        }
        Box lv = this.getBoundingBox().expand(10.0, 10.0, 10.0);
        List<VillagerEntity> list = arg2.getNonSpectatingEntities(VillagerEntity.class, lv);
        List list2 = list.stream().filter(arg -> arg.canSummonGolem(l)).limit(5L).collect(Collectors.toList());
        if (list2.size() < i) {
            return;
        }
        IronGolemEntity lv2 = this.spawnIronGolem(arg2);
        if (lv2 == null) {
            return;
        }
        list.forEach(GolemLastSeenSensor::method_30233);
    }

    public boolean canSummonGolem(long time) {
        if (!this.hasRecentlyWorkedAndSlept(this.world.getTime())) {
            return false;
        }
        return !this.brain.hasMemoryModule(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }

    @Nullable
    private IronGolemEntity spawnIronGolem(ServerWorld arg) {
        BlockPos lv = this.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            IronGolemEntity lv3;
            double e;
            double d = arg.random.nextInt(16) - 8;
            BlockPos lv2 = this.method_30023(lv, d, e = (double)(arg.random.nextInt(16) - 8));
            if (lv2 == null || (lv3 = EntityType.IRON_GOLEM.create(arg, null, null, null, lv2, SpawnReason.MOB_SUMMONED, false, false)) == null) continue;
            if (lv3.canSpawn(arg, SpawnReason.MOB_SUMMONED) && lv3.canSpawn(arg)) {
                arg.spawnEntity(lv3);
                return lv3;
            }
            lv3.remove();
        }
        return null;
    }

    @Nullable
    private BlockPos method_30023(BlockPos arg, double d, double e) {
        int i = 6;
        BlockPos lv = arg.add(d, 6.0, e);
        BlockState lv2 = this.world.getBlockState(lv);
        for (int j = 6; j >= -6; --j) {
            BlockPos lv3 = lv;
            BlockState lv4 = lv2;
            lv = lv3.down();
            lv2 = this.world.getBlockState(lv);
            if (!lv4.isAir() && !lv4.getMaterial().isLiquid() || !lv2.getMaterial().blocksLight()) continue;
            return lv3;
        }
        return null;
    }

    @Override
    public void onInteractionWith(EntityInteraction interaction, Entity entity) {
        if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_POSITIVE, 20);
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_POSITIVE, 25);
        } else if (interaction == EntityInteraction.TRADE) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.TRADING, 2);
        } else if (interaction == EntityInteraction.VILLAGER_HURT) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_NEGATIVE, 25);
        } else if (interaction == EntityInteraction.VILLAGER_KILLED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 25);
        }
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    public void setExperience(int amount) {
        this.experience = amount;
    }

    private void clearDailyRestockCount() {
        this.method_21723();
        this.restocksToday = 0;
    }

    public VillagerGossips getGossip() {
        return this.gossip;
    }

    public void setGossipDataFromTag(Tag arg) {
        this.gossip.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)arg));
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void sleep(BlockPos pos) {
        super.sleep(pos);
        this.brain.remember(MemoryModuleType.LAST_SLEPT, this.world.getTime());
        this.brain.forget(MemoryModuleType.WALK_TARGET);
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void wakeUp() {
        super.wakeUp();
        this.brain.remember(MemoryModuleType.LAST_WOKEN, this.world.getTime());
    }

    private boolean hasRecentlyWorkedAndSlept(long worldTime) {
        Optional<Long> optional = this.brain.getOptionalMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent()) {
            return worldTime - optional.get() < 24000L;
        }
        return false;
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        return this.createChild(arg, arg2);
    }
}

