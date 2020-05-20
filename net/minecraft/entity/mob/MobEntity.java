/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobVisibilityCache;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class MobEntity
extends LivingEntity {
    private static final TrackedData<Byte> MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
    public int ambientSoundChance;
    protected int experiencePoints;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyControl bodyControl;
    protected EntityNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    private LivingEntity target;
    private final MobVisibilityCache visibilityCache;
    private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    protected final float[] handDropChances = new float[2];
    private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    protected final float[] armorDropChances = new float[4];
    private boolean pickUpLoot;
    private boolean persistent;
    private final Map<PathNodeType, Float> pathfindingPenalties = Maps.newEnumMap(PathNodeType.class);
    private Identifier lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity holdingEntity;
    private int holdingEntityId;
    @Nullable
    private CompoundTag leashTag;
    private BlockPos positionTarget = BlockPos.ORIGIN;
    private float positionTargetRange = -1.0f;

    protected MobEntity(EntityType<? extends MobEntity> arg, World arg2) {
        super((EntityType<? extends LivingEntity>)arg, arg2);
        this.goalSelector = new GoalSelector(arg2.getProfilerSupplier());
        this.targetSelector = new GoalSelector(arg2.getProfilerSupplier());
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyControl = this.createBodyControl();
        this.navigation = this.createNavigation(arg2);
        this.visibilityCache = new MobVisibilityCache(this);
        Arrays.fill(this.armorDropChances, 0.085f);
        Arrays.fill(this.handDropChances, 0.085f);
        if (arg2 != null && !arg2.isClient) {
            this.initGoals();
        }
    }

    protected void initGoals() {
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
    }

    protected EntityNavigation createNavigation(World arg) {
        return new MobNavigation(this, arg);
    }

    protected boolean movesIndependently() {
        return false;
    }

    public float getPathfindingPenalty(PathNodeType arg) {
        MobEntity lv2;
        if (this.getVehicle() instanceof MobEntity && ((MobEntity)this.getVehicle()).movesIndependently()) {
            MobEntity lv = (MobEntity)this.getVehicle();
        } else {
            lv2 = this;
        }
        Float lv3 = lv2.pathfindingPenalties.get((Object)arg);
        return lv3 == null ? arg.getDefaultPenalty() : lv3.floatValue();
    }

    public void setPathfindingPenalty(PathNodeType arg, float f) {
        this.pathfindingPenalties.put(arg, Float.valueOf(f));
    }

    protected BodyControl createBodyControl() {
        return new BodyControl(this);
    }

    public LookControl getLookControl() {
        return this.lookControl;
    }

    public MoveControl getMoveControl() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            MobEntity lv = (MobEntity)this.getVehicle();
            return lv.getMoveControl();
        }
        return this.moveControl;
    }

    public JumpControl getJumpControl() {
        return this.jumpControl;
    }

    public EntityNavigation getNavigation() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            MobEntity lv = (MobEntity)this.getVehicle();
            return lv.getNavigation();
        }
        return this.navigation;
    }

    public MobVisibilityCache getVisibilityCache() {
        return this.visibilityCache;
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable LivingEntity arg) {
        this.target = arg;
    }

    @Override
    public boolean canTarget(EntityType<?> arg) {
        return arg != EntityType.GHAST;
    }

    public boolean canUseRangedWeapon(RangedWeaponItem arg) {
        return false;
    }

    public void onEatingGrass() {
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MOB_FLAGS, (byte)0);
    }

    public int getMinAmbientSoundDelay() {
        return 80;
    }

    public void playAmbientSound() {
        SoundEvent lv = this.getAmbientSound();
        if (lv != null) {
            this.playSound(lv, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.world.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
            this.resetSoundDelay();
            this.playAmbientSound();
        }
        this.world.getProfiler().pop();
    }

    @Override
    protected void playHurtSound(DamageSource arg) {
        this.resetSoundDelay();
        super.playHurtSound(arg);
    }

    private void resetSoundDelay() {
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    @Override
    protected int getCurrentExperience(PlayerEntity arg) {
        if (this.experiencePoints > 0) {
            int i = this.experiencePoints;
            for (int j = 0; j < this.armorItems.size(); ++j) {
                if (this.armorItems.get(j).isEmpty() || !(this.armorDropChances[j] <= 1.0f)) continue;
                i += 1 + this.random.nextInt(3);
            }
            for (int k = 0; k < this.handItems.size(); ++k) {
                if (this.handItems.get(k).isEmpty() || !(this.handDropChances[k] <= 1.0f)) continue;
                i += 1 + this.random.nextInt(3);
            }
            return i;
        }
        return this.experiencePoints;
    }

    public void playSpawnEffects() {
        if (this.world.isClient) {
            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                double g = 10.0;
                this.world.addParticle(ParticleTypes.POOF, this.offsetX(1.0) - d * 10.0, this.getRandomBodyY() - e * 10.0, this.getParticleZ(1.0) - f * 10.0, d, e, f);
            }
        } else {
            this.world.sendEntityStatus(this, (byte)20);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 20) {
            this.playSpawnEffects();
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.updateLeash();
            if (this.age % 5 == 0) {
                this.updateGoalControls();
            }
        }
    }

    protected void updateGoalControls() {
        boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity);
        boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
        this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
        this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
    }

    @Override
    protected float turnHead(float f, float g) {
        this.bodyControl.tick();
        return g;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        arg.putBoolean("PersistenceRequired", this.persistent);
        ListTag lv = new ListTag();
        for (ItemStack itemStack : this.armorItems) {
            CompoundTag compoundTag = new CompoundTag();
            if (!itemStack.isEmpty()) {
                itemStack.toTag(compoundTag);
            }
            lv.add(compoundTag);
        }
        arg.put("ArmorItems", lv);
        ListTag lv4 = new ListTag();
        for (ItemStack itemStack : this.handItems) {
            CompoundTag lv6 = new CompoundTag();
            if (!itemStack.isEmpty()) {
                itemStack.toTag(lv6);
            }
            lv4.add(lv6);
        }
        arg.put("HandItems", lv4);
        ListTag listTag = new ListTag();
        for (float f : this.armorDropChances) {
            listTag.add(FloatTag.of(f));
        }
        arg.put("ArmorDropChances", listTag);
        ListTag listTag2 = new ListTag();
        for (float g : this.handDropChances) {
            listTag2.add(FloatTag.of(g));
        }
        arg.put("HandDropChances", listTag2);
        if (this.holdingEntity != null) {
            CompoundTag lv9 = new CompoundTag();
            if (this.holdingEntity instanceof LivingEntity) {
                UUID uUID = this.holdingEntity.getUuid();
                lv9.putUuidNew("UUID", uUID);
            } else if (this.holdingEntity instanceof AbstractDecorationEntity) {
                BlockPos lv10 = ((AbstractDecorationEntity)this.holdingEntity).getDecorationBlockPos();
                lv9.putInt("X", lv10.getX());
                lv9.putInt("Y", lv10.getY());
                lv9.putInt("Z", lv10.getZ());
            }
            arg.put("Leash", lv9);
        } else if (this.leashTag != null) {
            arg.put("Leash", this.leashTag.copy());
        }
        arg.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            arg.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                arg.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isAiDisabled()) {
            arg.putBoolean("NoAI", this.isAiDisabled());
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(arg.getBoolean("CanPickUpLoot"));
        }
        this.persistent = arg.getBoolean("PersistenceRequired");
        if (arg.contains("ArmorItems", 9)) {
            ListTag lv = arg.getList("ArmorItems", 10);
            for (int i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.fromTag(lv.getCompound(i)));
            }
        }
        if (arg.contains("HandItems", 9)) {
            ListTag lv2 = arg.getList("HandItems", 10);
            for (int j = 0; j < this.handItems.size(); ++j) {
                this.handItems.set(j, ItemStack.fromTag(lv2.getCompound(j)));
            }
        }
        if (arg.contains("ArmorDropChances", 9)) {
            ListTag lv3 = arg.getList("ArmorDropChances", 5);
            for (int k = 0; k < lv3.size(); ++k) {
                this.armorDropChances[k] = lv3.getFloat(k);
            }
        }
        if (arg.contains("HandDropChances", 9)) {
            ListTag lv4 = arg.getList("HandDropChances", 5);
            for (int l = 0; l < lv4.size(); ++l) {
                this.handDropChances[l] = lv4.getFloat(l);
            }
        }
        if (arg.contains("Leash", 10)) {
            this.leashTag = arg.getCompound("Leash");
        }
        this.setLeftHanded(arg.getBoolean("LeftHanded"));
        if (arg.contains("DeathLootTable", 8)) {
            this.lootTable = new Identifier(arg.getString("DeathLootTable"));
            this.lootTableSeed = arg.getLong("DeathLootTableSeed");
        }
        this.setAiDisabled(arg.getBoolean("NoAI"));
    }

    @Override
    protected void dropLoot(DamageSource arg, boolean bl) {
        super.dropLoot(arg, bl);
        this.lootTable = null;
    }

    @Override
    protected LootContext.Builder getLootContextBuilder(boolean bl, DamageSource arg) {
        return super.getLootContextBuilder(bl, arg).random(this.lootTableSeed, this.random);
    }

    @Override
    public final Identifier getLootTable() {
        return this.lootTable == null ? this.getLootTableId() : this.lootTable;
    }

    protected Identifier getLootTableId() {
        return super.getLootTable();
    }

    public void setForwardSpeed(float f) {
        this.forwardSpeed = f;
    }

    public void setUpwardSpeed(float f) {
        this.upwardSpeed = f;
    }

    public void setSidewaysSpeed(float f) {
        this.sidewaysSpeed = f;
    }

    @Override
    public void setMovementSpeed(float f) {
        super.setMovementSpeed(f);
        this.setForwardSpeed(f);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.world.getProfiler().push("looting");
        if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            List<ItemEntity> list = this.world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0));
            for (ItemEntity lv : list) {
                if (lv.removed || lv.getStack().isEmpty() || lv.cannotPickup() || !this.canGather(lv.getStack())) continue;
                this.loot(lv);
            }
        }
        this.world.getProfiler().pop();
    }

    protected void loot(ItemEntity arg) {
        ItemStack lv = arg.getStack();
        if (this.tryEquip(lv)) {
            this.method_27964(arg);
            this.sendPickup(arg, lv.getCount());
            arg.remove();
        }
    }

    protected void method_27964(ItemEntity arg) {
        PlayerEntity lv;
        PlayerEntity playerEntity = lv = arg.getThrower() != null ? this.world.getPlayerByUuid(arg.getThrower()) : null;
        if (lv instanceof ServerPlayerEntity) {
            Criteria.THROWN_ITEM_PICKED_UP_BY_ENTITY.test((ServerPlayerEntity)lv, arg.getStack(), this);
        }
    }

    public boolean tryEquip(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        ItemStack lv2 = this.getEquippedStack(lv);
        boolean bl = this.prefersNewEquipment(arg, lv2);
        if (bl && this.canPickupItem(arg)) {
            double d = this.getDropChance(lv);
            if (!lv2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.dropStack(lv2);
            }
            this.equipLootStack(lv, arg);
            this.onEquipStack(arg);
            return true;
        }
        return false;
    }

    protected void equipLootStack(EquipmentSlot arg, ItemStack arg2) {
        this.equipStack(arg, arg2);
        this.updateDropChances(arg);
        this.persistent = true;
    }

    public void updateDropChances(EquipmentSlot arg) {
        switch (arg.getType()) {
            case HAND: {
                this.handDropChances[arg.getEntitySlotId()] = 2.0f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[arg.getEntitySlotId()] = 2.0f;
            }
        }
    }

    protected boolean prefersNewEquipment(ItemStack arg, ItemStack arg2) {
        if (arg2.isEmpty()) {
            return true;
        }
        if (arg.getItem() instanceof SwordItem) {
            if (!(arg2.getItem() instanceof SwordItem)) {
                return true;
            }
            SwordItem lv = (SwordItem)arg.getItem();
            SwordItem lv2 = (SwordItem)arg2.getItem();
            if (lv.getAttackDamage() != lv2.getAttackDamage()) {
                return lv.getAttackDamage() > lv2.getAttackDamage();
            }
            return this.prefersNewDamageableItem(arg, arg2);
        }
        if (arg.getItem() instanceof BowItem && arg2.getItem() instanceof BowItem) {
            return this.prefersNewDamageableItem(arg, arg2);
        }
        if (arg.getItem() instanceof CrossbowItem && arg2.getItem() instanceof CrossbowItem) {
            return this.prefersNewDamageableItem(arg, arg2);
        }
        if (arg.getItem() instanceof ArmorItem) {
            if (EnchantmentHelper.hasBindingCurse(arg2)) {
                return false;
            }
            if (!(arg2.getItem() instanceof ArmorItem)) {
                return true;
            }
            ArmorItem lv3 = (ArmorItem)arg.getItem();
            ArmorItem lv4 = (ArmorItem)arg2.getItem();
            if (lv3.getProtection() != lv4.getProtection()) {
                return lv3.getProtection() > lv4.getProtection();
            }
            if (lv3.method_26353() != lv4.method_26353()) {
                return lv3.method_26353() > lv4.method_26353();
            }
            return this.prefersNewDamageableItem(arg, arg2);
        }
        if (arg.getItem() instanceof MiningToolItem) {
            if (arg2.getItem() instanceof BlockItem) {
                return true;
            }
            if (arg2.getItem() instanceof MiningToolItem) {
                MiningToolItem lv5 = (MiningToolItem)arg.getItem();
                MiningToolItem lv6 = (MiningToolItem)arg2.getItem();
                if (lv5.getAttackDamage() != lv6.getAttackDamage()) {
                    return lv5.getAttackDamage() > lv6.getAttackDamage();
                }
                return this.prefersNewDamageableItem(arg, arg2);
            }
        }
        return false;
    }

    public boolean prefersNewDamageableItem(ItemStack arg, ItemStack arg2) {
        if (arg.getDamage() < arg2.getDamage() || arg.hasTag() && !arg2.hasTag()) {
            return true;
        }
        if (arg.hasTag() && arg2.hasTag()) {
            return arg.getTag().getKeys().stream().anyMatch(string -> !string.equals("Damage")) && !arg2.getTag().getKeys().stream().anyMatch(string -> !string.equals("Damage"));
        }
        return false;
    }

    public boolean canPickupItem(ItemStack arg) {
        return true;
    }

    public boolean canGather(ItemStack arg) {
        return this.canPickupItem(arg);
    }

    public boolean canImmediatelyDespawn(double d) {
        return true;
    }

    public boolean cannotDespawn() {
        return this.hasVehicle();
    }

    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
            this.remove();
            return;
        }
        if (this.isPersistent() || this.cannotDespawn()) {
            this.despawnCounter = 0;
            return;
        }
        PlayerEntity lv = this.world.getClosestPlayer(this, -1.0);
        if (lv != null) {
            int i;
            int j;
            double d = lv.squaredDistanceTo(this);
            if (d > (double)(j = (i = this.getType().getSpawnGroup().getImmediateDespawnRange()) * i) && this.canImmediatelyDespawn(d)) {
                this.remove();
            }
            int k = this.getType().getSpawnGroup().getDespawnStartRange();
            int l = k * k;
            if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > (double)l && this.canImmediatelyDespawn(d)) {
                this.remove();
            } else if (d < (double)l) {
                this.despawnCounter = 0;
            }
        }
    }

    @Override
    protected final void tickNewAi() {
        ++this.despawnCounter;
        this.world.getProfiler().push("sensing");
        this.visibilityCache.clear();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("targetSelector");
        this.targetSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("goalSelector");
        this.goalSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("navigation");
        this.navigation.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("mob tick");
        this.mobTick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("controls");
        this.world.getProfiler().push("move");
        this.moveControl.tick();
        this.world.getProfiler().swap("look");
        this.lookControl.tick();
        this.world.getProfiler().swap("jump");
        this.jumpControl.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().pop();
        this.sendAiDebugData();
    }

    protected void sendAiDebugData() {
        DebugInfoSender.sendGoalSelector(this.world, this, this.goalSelector);
    }

    protected void mobTick() {
    }

    public int getLookPitchSpeed() {
        return 40;
    }

    public int getBodyYawSpeed() {
        return 75;
    }

    public int getLookYawSpeed() {
        return 10;
    }

    public void lookAtEntity(Entity arg, float f, float g) {
        double i;
        double d = arg.getX() - this.getX();
        double e = arg.getZ() - this.getZ();
        if (arg instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)arg;
            double h = lv.getEyeY() - this.getEyeY();
        } else {
            i = (arg.getBoundingBox().minY + arg.getBoundingBox().maxY) / 2.0 - this.getEyeY();
        }
        double j = MathHelper.sqrt(d * d + e * e);
        float k = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
        float l = (float)(-(MathHelper.atan2(i, j) * 57.2957763671875));
        this.pitch = this.changeAngle(this.pitch, l, g);
        this.yaw = this.changeAngle(this.yaw, k, f);
    }

    private float changeAngle(float f, float g, float h) {
        float i = MathHelper.wrapDegrees(g - f);
        if (i > h) {
            i = h;
        }
        if (i < -h) {
            i = -h;
        }
        return f + i;
    }

    public static boolean canMobSpawn(EntityType<? extends MobEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        BlockPos lv = arg4.down();
        return arg3 == SpawnReason.SPAWNER || arg2.getBlockState(lv).allowsSpawning(arg2, lv, arg);
    }

    public boolean canSpawn(WorldAccess arg, SpawnReason arg2) {
        return true;
    }

    public boolean canSpawn(WorldView arg) {
        return !arg.containsFluid(this.getBoundingBox()) && arg.intersectsEntities(this);
    }

    public int getLimitPerChunk() {
        return 4;
    }

    public boolean spawnsTooManyForEachTry(int i) {
        return false;
    }

    @Override
    public int getSafeFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        int i = (int)(this.getHealth() - this.getMaximumHealth() * 0.33f);
        if ((i -= (3 - this.world.getDifficulty().getId()) * 4) < 0) {
            i = 0;
        }
        return i + 3;
    }

    @Override
    public Iterable<ItemStack> getItemsHand() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot arg) {
        switch (arg.getType()) {
            case HAND: {
                return this.handItems.get(arg.getEntitySlotId());
            }
            case ARMOR: {
                return this.armorItems.get(arg.getEntitySlotId());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot arg, ItemStack arg2) {
        switch (arg.getType()) {
            case HAND: {
                this.handItems.set(arg.getEntitySlotId(), arg2);
                break;
            }
            case ARMOR: {
                this.armorItems.set(arg.getEntitySlotId(), arg2);
            }
        }
    }

    @Override
    protected void dropEquipment(DamageSource arg, int i, boolean bl) {
        super.dropEquipment(arg, i, bl);
        for (EquipmentSlot lv : EquipmentSlot.values()) {
            boolean bl2;
            ItemStack lv2 = this.getEquippedStack(lv);
            float f = this.getDropChance(lv);
            boolean bl3 = bl2 = f > 1.0f;
            if (lv2.isEmpty() || EnchantmentHelper.hasVanishingCurse(lv2) || !bl && !bl2 || !(Math.max(this.random.nextFloat() - (float)i * 0.01f, 0.0f) < f)) continue;
            if (!bl2 && lv2.isDamageable()) {
                lv2.setDamage(lv2.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(lv2.getMaxDamage() - 3, 1))));
            }
            this.dropStack(lv2);
        }
    }

    protected float getDropChance(EquipmentSlot arg) {
        float h;
        switch (arg.getType()) {
            case HAND: {
                float f = this.handDropChances[arg.getEntitySlotId()];
                break;
            }
            case ARMOR: {
                float g = this.armorDropChances[arg.getEntitySlotId()];
                break;
            }
            default: {
                h = 0.0f;
            }
        }
        return h;
    }

    protected void initEquipment(LocalDifficulty arg) {
        if (this.random.nextFloat() < 0.15f * arg.getClampedLocalDifficulty()) {
            float f;
            int i = this.random.nextInt(2);
            float f2 = f = this.world.getDifficulty() == Difficulty.HARD ? 0.1f : 0.25f;
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            boolean bl = true;
            for (EquipmentSlot lv : EquipmentSlot.values()) {
                Item lv3;
                if (lv.getType() != EquipmentSlot.Type.ARMOR) continue;
                ItemStack lv2 = this.getEquippedStack(lv);
                if (!bl && this.random.nextFloat() < f) break;
                bl = false;
                if (!lv2.isEmpty() || (lv3 = MobEntity.getEquipmentForSlot(lv, i)) == null) continue;
                this.equipStack(lv, new ItemStack(lv3));
            }
        }
    }

    public static EquipmentSlot getPreferredEquipmentSlot(ItemStack arg) {
        Item lv = arg.getItem();
        if (lv == Blocks.CARVED_PUMPKIN.asItem() || lv instanceof BlockItem && ((BlockItem)lv).getBlock() instanceof AbstractSkullBlock) {
            return EquipmentSlot.HEAD;
        }
        if (lv instanceof ArmorItem) {
            return ((ArmorItem)lv).getSlotType();
        }
        if (lv == Items.ELYTRA) {
            return EquipmentSlot.CHEST;
        }
        if (lv == Items.SHIELD) {
            return EquipmentSlot.OFFHAND;
        }
        return EquipmentSlot.MAINHAND;
    }

    @Nullable
    public static Item getEquipmentForSlot(EquipmentSlot arg, int i) {
        switch (arg) {
            case HEAD: {
                if (i == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (i == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (i == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (i == 3) {
                    return Items.IRON_HELMET;
                }
                if (i == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if (i == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (i == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (i == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (i == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (i == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if (i == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (i == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (i == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (i == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (i == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if (i == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (i == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (i == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (i == 3) {
                    return Items.IRON_BOOTS;
                }
                if (i != 4) break;
                return Items.DIAMOND_BOOTS;
            }
        }
        return null;
    }

    protected void updateEnchantments(LocalDifficulty arg) {
        float f = arg.getClampedLocalDifficulty();
        if (!this.getMainHandStack().isEmpty() && this.random.nextFloat() < 0.25f * f) {
            this.equipStack(EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(this.random, this.getMainHandStack(), (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
        for (EquipmentSlot lv : EquipmentSlot.values()) {
            ItemStack lv2;
            if (lv.getType() != EquipmentSlot.Type.ARMOR || (lv2 = this.getEquippedStack(lv)).isEmpty() || !(this.random.nextFloat() < 0.5f * f)) continue;
            this.equipStack(lv, EnchantmentHelper.enchant(this.random, lv2, (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
    }

    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return arg4;
    }

    public boolean canBeControlledByRider() {
        return false;
    }

    public void setPersistent() {
        this.persistent = true;
    }

    public void setEquipmentDropChance(EquipmentSlot arg, float f) {
        switch (arg.getType()) {
            case HAND: {
                this.handDropChances[arg.getEntitySlotId()] = f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[arg.getEntitySlotId()] = f;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.pickUpLoot;
    }

    public void setCanPickUpLoot(boolean bl) {
        this.pickUpLoot = bl;
    }

    @Override
    public boolean canPickUp(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        return this.getEquippedStack(lv).isEmpty() && this.canPickUpLoot();
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    @Override
    public final boolean interact(PlayerEntity arg, Hand arg2) {
        if (!this.isAlive()) {
            return false;
        }
        if (this.getHoldingEntity() == arg) {
            this.detachLeash(true, !arg.abilities.creativeMode);
            return true;
        }
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.getItem() == Items.LEAD && this.canBeLeashedBy(arg)) {
            this.attachLeash(arg, true);
            lv.decrement(1);
            return true;
        }
        if (lv.getItem() == Items.NAME_TAG) {
            lv.useOnEntity(arg, this, arg2);
            return true;
        }
        if (this.interactMob(arg, arg2)) {
            return true;
        }
        return super.interact(arg, arg2);
    }

    protected void onPlayerSpawnedChild(PlayerEntity arg, MobEntity arg2) {
    }

    protected boolean interactMob(PlayerEntity arg, Hand arg22) {
        ItemStack lv = arg.getStackInHand(arg22);
        Item lv2 = lv.getItem();
        if (!this.world.isClient && lv2 instanceof SpawnEggItem) {
            SpawnEggItem lv3 = (SpawnEggItem)lv2;
            Optional<MobEntity> optional = lv3.spawnBaby(arg, this, this.getType(), this.world, this.getPos(), lv);
            optional.ifPresent(arg2 -> this.onPlayerSpawnedChild(arg, (MobEntity)arg2));
        }
        return false;
    }

    public boolean isInWalkTargetRange() {
        return this.isInWalkTargetRange(this.getBlockPos());
    }

    public boolean isInWalkTargetRange(BlockPos arg) {
        if (this.positionTargetRange == -1.0f) {
            return true;
        }
        return this.positionTarget.getSquaredDistance(arg) < (double)(this.positionTargetRange * this.positionTargetRange);
    }

    public void setPositionTarget(BlockPos arg, int i) {
        this.positionTarget = arg;
        this.positionTargetRange = i;
    }

    public BlockPos getPositionTarget() {
        return this.positionTarget;
    }

    public float getPositionTargetRange() {
        return this.positionTargetRange;
    }

    public boolean hasPositionTarget() {
        return this.positionTargetRange != -1.0f;
    }

    protected void updateLeash() {
        if (this.leashTag != null) {
            this.deserializeLeashTag();
        }
        if (this.holdingEntity == null) {
            return;
        }
        if (!this.isAlive() || !this.holdingEntity.isAlive()) {
            this.detachLeash(true, true);
        }
    }

    public void detachLeash(boolean bl, boolean bl2) {
        if (this.holdingEntity != null) {
            this.teleporting = false;
            if (!(this.holdingEntity instanceof PlayerEntity)) {
                this.holdingEntity.teleporting = false;
            }
            this.holdingEntity = null;
            this.leashTag = null;
            if (!this.world.isClient && bl2) {
                this.dropItem(Items.LEAD);
            }
            if (!this.world.isClient && bl && this.world instanceof ServerWorld) {
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, null));
            }
        }
    }

    public boolean canBeLeashedBy(PlayerEntity arg) {
        return !this.isLeashed() && !(this instanceof Monster);
    }

    public boolean isLeashed() {
        return this.holdingEntity != null;
    }

    @Nullable
    public Entity getHoldingEntity() {
        if (this.holdingEntity == null && this.holdingEntityId != 0 && this.world.isClient) {
            this.holdingEntity = this.world.getEntityById(this.holdingEntityId);
        }
        return this.holdingEntity;
    }

    public void attachLeash(Entity arg, boolean bl) {
        this.holdingEntity = arg;
        this.leashTag = null;
        this.teleporting = true;
        if (!(this.holdingEntity instanceof PlayerEntity)) {
            this.holdingEntity.teleporting = true;
        }
        if (!this.world.isClient && bl && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void setHoldingEntityId(int i) {
        this.holdingEntityId = i;
        this.detachLeash(false, false);
    }

    @Override
    public boolean startRiding(Entity arg, boolean bl) {
        boolean bl2 = super.startRiding(arg, bl);
        if (bl2 && this.isLeashed()) {
            this.detachLeash(true, true);
        }
        return bl2;
    }

    private void deserializeLeashTag() {
        if (this.leashTag != null && this.world instanceof ServerWorld) {
            if (this.leashTag.containsUuidNew("UUID")) {
                UUID uUID = this.leashTag.getUuidNew("UUID");
                Entity lv = ((ServerWorld)this.world).getEntity(uUID);
                if (lv != null) {
                    this.attachLeash(lv, true);
                }
            } else if (this.leashTag.contains("X", 99) && this.leashTag.contains("Y", 99) && this.leashTag.contains("Z", 99)) {
                BlockPos lv2 = new BlockPos(this.leashTag.getInt("X"), this.leashTag.getInt("Y"), this.leashTag.getInt("Z"));
                this.attachLeash(LeashKnotEntity.getOrCreate(this.world, lv2), true);
            } else {
                this.detachLeash(false, true);
            }
            if (this.age > 100) {
                this.leashTag = null;
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean equip(int i, ItemStack arg) {
        void lv7;
        if (i == 98) {
            EquipmentSlot lv = EquipmentSlot.MAINHAND;
        } else if (i == 99) {
            EquipmentSlot lv2 = EquipmentSlot.OFFHAND;
        } else if (i == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            EquipmentSlot lv3 = EquipmentSlot.HEAD;
        } else if (i == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            EquipmentSlot lv4 = EquipmentSlot.CHEST;
        } else if (i == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            EquipmentSlot lv5 = EquipmentSlot.LEGS;
        } else if (i == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            EquipmentSlot lv6 = EquipmentSlot.FEET;
        } else {
            return false;
        }
        if (arg.isEmpty() || MobEntity.canEquipmentSlotContain((EquipmentSlot)lv7, arg) || lv7 == EquipmentSlot.HEAD) {
            this.equipStack((EquipmentSlot)lv7, arg);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        return this.canBeControlledByRider() && super.isLogicalSideForUpdatingMovement();
    }

    public static boolean canEquipmentSlotContain(EquipmentSlot arg, ItemStack arg2) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg2);
        return lv == arg || lv == EquipmentSlot.MAINHAND && arg == EquipmentSlot.OFFHAND || lv == EquipmentSlot.OFFHAND && arg == EquipmentSlot.MAINHAND;
    }

    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && !this.isAiDisabled();
    }

    public void setAiDisabled(boolean bl) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, bl ? (byte)(b | 1) : (byte)(b & 0xFFFFFFFE));
    }

    public void setLeftHanded(boolean bl) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, bl ? (byte)(b | 2) : (byte)(b & 0xFFFFFFFD));
    }

    public void setAttacking(boolean bl) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, bl ? (byte)(b | 4) : (byte)(b & 0xFFFFFFFB));
    }

    public boolean isAiDisabled() {
        return (this.dataTracker.get(MOB_FLAGS) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return (this.dataTracker.get(MOB_FLAGS) & 2) != 0;
    }

    public boolean isAttacking() {
        return (this.dataTracker.get(MOB_FLAGS) & 4) != 0;
    }

    public void setBaby(boolean bl) {
    }

    @Override
    public Arm getMainArm() {
        return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
    }

    @Override
    public boolean canTarget(LivingEntity arg) {
        if (arg.getType() == EntityType.PLAYER && ((PlayerEntity)arg).abilities.invulnerable) {
            return false;
        }
        return super.canTarget(arg);
    }

    @Override
    public boolean tryAttack(Entity arg) {
        boolean bl;
        int i;
        float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float g = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        if (arg instanceof LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)arg).getGroup());
            g += (float)EnchantmentHelper.getKnockback(this);
        }
        if ((i = EnchantmentHelper.getFireAspect(this)) > 0) {
            arg.setOnFireFor(i * 4);
        }
        if (bl = arg.damage(DamageSource.mob(this), f)) {
            if (g > 0.0f && arg instanceof LivingEntity) {
                ((LivingEntity)arg).takeKnockback(g * 0.5f, MathHelper.sin(this.yaw * ((float)Math.PI / 180)), -MathHelper.cos(this.yaw * ((float)Math.PI / 180)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }
            if (arg instanceof PlayerEntity) {
                PlayerEntity lv;
                this.disablePlayerShield(lv, this.getMainHandStack(), (lv = (PlayerEntity)arg).isUsingItem() ? lv.getActiveItem() : ItemStack.EMPTY);
            }
            this.dealDamage(this, arg);
            this.onAttacking(arg);
        }
        return bl;
    }

    private void disablePlayerShield(PlayerEntity arg, ItemStack arg2, ItemStack arg3) {
        if (!arg2.isEmpty() && !arg3.isEmpty() && arg2.getItem() instanceof AxeItem && arg3.getItem() == Items.SHIELD) {
            float f = 0.25f + (float)EnchantmentHelper.getEfficiency(this) * 0.05f;
            if (this.random.nextFloat() < f) {
                arg.getItemCooldownManager().set(Items.SHIELD, 100);
                this.world.sendEntityStatus(arg, (byte)30);
            }
        }
    }

    protected boolean isInDaylight() {
        if (this.world.isDay() && !this.world.isClient) {
            BlockPos lv;
            float f = this.getBrightnessAtEyes();
            BlockPos blockPos = lv = this.getVehicle() instanceof BoatEntity ? new BlockPos(this.getX(), Math.round(this.getY()), this.getZ()).up() : new BlockPos(this.getX(), Math.round(this.getY()), this.getZ());
            if (f > 0.5f && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.world.isSkyVisible(lv)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void swimUpward(Tag<Fluid> arg) {
        if (this.getNavigation().canSwim()) {
            super.swimUpward(arg);
        } else {
            this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
        }
    }
}

